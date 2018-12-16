
import java.io.*;
import java.util.*;

public class intepreter {
    private static boolean isTrue;

    public static void interpreter(String input, String output) {
        try {
            BufferedReader br;
            BufferedWriter bo;
            HashMap<String, String> p1;
            HashMap<String, Helper> p2;
            Stack<String> writeToFile;
            Helper temp = null;
            p1 = new HashMap<>();
            p2 = new HashMap<>();
            br = new BufferedReader(new FileReader(input));
            bo = new BufferedWriter(new FileWriter(output));
            writeToFile = core(br, temp, p1, p2);

            for (; !writeToFile.isEmpty(); ) {
                if (!isString((writeToFile.peek()))) {
                    bo.write(writeToFile.pop() + '\n');
                } else {
                    bo.write((writeToFile.pop().replaceAll("\"", "")) + "\n");
                }
            }
            br.close();
            bo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Stack<String> core(BufferedReader br, Helper c, HashMap<String, String> var,
                                      HashMap<String, Helper> tf) throws Exception {
        Stack<String> writeToFile = new Stack<String>();
        String line = re(br, c);
        Helper rf = null;
        int rfn = 0;
        Helper func = null;
        while (line != null) {
            String tempCD[] = line.split("\\s+");
            if (rf != null) {
                rf.addCommand(line);
                if (tempCD[0].equals("fun")) {
                    rfn++;
                } else if (tempCD[0].equals("inOutFun")) {
                    rfn++;
                } else if (tempCD[0].equals("funEnd")) {
                    rfn--;
                }

                if (!tempCD[0].equals("funEnd")) {
                    line = re(br, c);
                    continue;
                }
            }
            switch (tempCD[0]) {
                case "inOutFun":
                    func = new Helper(tempCD[1], Arrays.asList(tempCD[2]), var, true);
                    tf.put(tempCD[1], func);
                    rf = func;
                    rfn++;
                    break;
                case "fun":
                    func = new Helper(tempCD[1], Arrays.asList(tempCD[2]), var, false);
                    tf.put(tempCD[1], func);
                    rf = func;
                    rfn++;
                    break;

                case "funEnd":
                    if (c != null) {
                        if (rf == null) {
                            return retF(writeToFile, var, c);
                        }
                    } else {
                        rf = null;
                        writeToFile.push(":unit:");
                    }
                    break;

                case "call":
                    boolean t = false;
                    Helper nf;
                    String fn;
                    Stack<String> tr;
                    boolean tmp;
                    if (writeToFile.isEmpty()) {
                        writeToFile.push(":error:");
                        break;
                    }
                    String arg = writeToFile.pop();
                    if (isInhere(var, tf, arg)) {
                        writeToFile.push(arg);
                        writeToFile.push(":error:");
                        break;
                    }

                    String val = arg;
                    if (var.containsKey(arg)) {
                        val = var.get(arg);
                    }

                    if (writeToFile.isEmpty()) {
                        writeToFile.push(arg);
                        writeToFile.push(":error:");
                        break;
                    }
                    if (!tf.containsKey(writeToFile.peek()) || rfn > 0) {
                        writeToFile.push(arg);
                        writeToFile.push(":error:");
                        isTrue = isT(t);
                        break;

                    }

                    fn = writeToFile.pop();
                    nf = new Helper(tf.get(fn));
                    tmp = nf.isIn;
                    tr = core(br, nf, nf.addE(Arrays.asList(val)), new HashMap<>(tf));
                    writeToFile.push(tr.pop());
                    if (tmp) {
                        if (var.containsKey(arg)) {
                            var.put(arg, writeToFile.pop());
                        }
                    }
                    if (isTrue) {
                        writeToFile.pop();
                        writeToFile.push("6");
                    }
                    break;

                case "return":
                    return retF(writeToFile, var, c);

                case "let":
                    tr = core(br, c, new HashMap<>(var), new HashMap<>(tf));
                    writeToFile.push(tr.pop());
                    break;

                case "end":
                    return writeToFile;

                case "if":
                    ifs(writeToFile, var);
                    break;

                case "and":
                    and(writeToFile, var);
                    break;

                case "or":
                    or(writeToFile, var);
                    break;

                case "not":
                    not(writeToFile, var);
                    break;

                case "equal":
                    equal(writeToFile, var);
                    break;

                case "lessThan":
                    lessThan(writeToFile, var);
                    break;

                case "bind":
                    bind(writeToFile, var);
                    break;

                case "push":
                    if (tempCD.length <= 1) {
                        writeToFile.push(":error:");
                        break;
                    }
                    arg = line.substring(tempCD[0].length() + 1);
                    if (arg.equals(":true:") || arg.equals(":false:")) {
                        writeToFile.push(arg);
                    } else if (arg.startsWith("\"") && arg.endsWith("\"")) {
                        writeToFile.push(arg);
                    } else if (arg.equals("0") || arg.equals("-0")) {
                        writeToFile.push("0");
                    } else if (arg.matches("[0-9]+")) {
                        writeToFile.push(arg);
                    } else if (arg.charAt(0) == ('-') && Character.isDigit(arg.charAt(1))) {
                        boolean tempBool = false;
                        for (char x : arg.toCharArray()) {
                            if (x == '.') {
                                tempBool = true;
                            }
                        }
                        if (tempBool) {
                            writeToFile.push(":error:");
                        } else {
                            writeToFile.push(arg);
                        }
                    } else if (arg.matches("[a-zA-Z]+") && !arg.contains(" ")) {
                        writeToFile.push(arg);

                    } else if (var.containsKey(arg)) {
                        if (c != null) {

                            if (c.getArgName().contains(arg)) {
                                writeToFile.push(var.get(arg));
                            }
                        } else {
                            writeToFile.push(arg);

                        }
                    } else if (tf.containsKey(arg) || var.containsKey(arg) || Character.isLetter(arg.charAt(0))) {
                        writeToFile.push(arg);
                    } else {
                        writeToFile.push(":error:");
                    }
                    break;

                case "cat":
                    cat(writeToFile, var);
                    break;
                case "add":
                    add(writeToFile, var);
                    break;
                case "mul":
                    mul(writeToFile, var);
                    break;
                case "sub":
                    sub(writeToFile, var);
                    break;
                case "div":
                    div(writeToFile, var);
                    break;
                case "rem":
                    rem(writeToFile, var);
                    break;
                case "neg":
                    neg(writeToFile, var);
                    break;
                case "pop":
                    pop(writeToFile);
                    break;
                case "swap":
                    swap(writeToFile);
                    break;
                case "quit":
                    return writeToFile;
                default:
                    writeToFile.push(":error:");
            }
            line = re(br, c);
        }
        return writeToFile;
    }

    private static boolean isT(boolean t) {
        return t = true;

    }


    private static Stack<String> retF(Stack<String> writeToFile, HashMap<String, String> variable, Helper h) {

        if (h.isIn) {
            for (String arg : h.getArgName()) {
                while (variable.containsKey(arg))
                    arg = variable.get(arg);
                writeToFile.push(arg);
            }
        } else {
            if (variable.containsKey(writeToFile.peek())) {
                if (h.getArgName().contains(writeToFile.peek())) {
                    String peek = writeToFile.peek();
                    writeToFile.pop();
                    if (variable.containsKey(peek)) {
                        peek = variable.get(peek);
                    }
                    writeToFile.push(peek);
                }
            }
        }

        return writeToFile;
    }

    private static String re(BufferedReader br, Helper cm) throws IOException {
        if (cm != null) {
            return cm.readLine();
        } else {
            return br.readLine();
        }
    }

    private static void ifs(Stack<String> writeToFile, HashMap<String, String> variable) {

        String tmpStr27;
        String tmpStr28;
        String tmpStr29;
        if (writeToFile.size() == 0) {
            writeToFile.push(":error:");
            return;
        } else if (writeToFile.size() < 3) {
            writeToFile.push(":error:");
            return;
        } else {
            tmpStr27 = writeToFile.pop();
            tmpStr28 = writeToFile.pop();
            tmpStr29 = writeToFile.pop();
            if (isBoolean(tmpStr29)) {
                if (tmpStr29.equals(":true:")) {
                    writeToFile.push(tmpStr28);
                    return;
                } else {
                    writeToFile.push(tmpStr27);
                    return;
                }
            } else {
                if (variable.containsKey(tmpStr29)) {
                    tmpStr29 = variable.get(tmpStr29);
                    if (isBoolean(tmpStr29)) {
                        if (tmpStr29.equals(":true:")) {
                            writeToFile.push(tmpStr28);
                            return;
                        } else {
                            writeToFile.push(tmpStr27);
                            return;
                        }
                    } else {
                        writeToFile.push(tmpStr29);
                        writeToFile.push(tmpStr28);
                        writeToFile.push(tmpStr27);
                        writeToFile.push(":error:");
                        return;
                    }
                }
            }
        }
    }

    private static void rem(Stack<String> writeToFile, HashMap<String, String> variable) {
        String tempStr9;
        String tempStr10;
        tempStr9 = isValid(writeToFile, variable, true, true, false, false);
        tempStr10 = isValid(writeToFile, variable, true, true, false, false);
        if (isNumeric(tempStr9)) {
            if (writeToFile.size() <= 1) {
                if (tempStr9 != null) {
                    if (variable.containsKey(tempStr9)) {
                        tempStr10 = variable.get(tempStr10);
                        if (isNumeric(tempStr10)) {
                            if (variable.containsKey(writeToFile.peek())) {
                                writeToFile.pop();
                                writeToFile.push("0");
                            }
                        }
                        writeToFile.push(tempStr10);
                        writeToFile.push(":error:");
                        return;
                    }
                }
            } else {
                writeToFile.push(":error:");
                return;
            }
            if (tempStr10 != null) {
                if (variable.containsKey(tempStr10)) {
                    writeToFile.push(tempStr10);
                    writeToFile.push(tempStr9);
                    writeToFile.push(":error:");
                    return;
                }
            } else {
                writeToFile.push(tempStr9);
                writeToFile.push(":error:");
                return;
            }
        } else {
            writeToFile.push(":error:");
            return;
        }

        writeToFile.push(Integer.toString(Integer.parseInt(tempStr10) % Integer.parseInt(tempStr9)));
    }

    private static void swap(Stack<String> writeToFile) {
        //for(;!writeToFile.isEmpty();) {System.out.println("--->   "+writeToFile.pop());}
        String n1, n2;
        if (!writeToFile.isEmpty()) {
            if (writeToFile.size() == 1) {
                writeToFile.push(":error:");
            } else {
                n1 = writeToFile.pop();
                if (writeToFile.size() != 0) {
                    n2 = writeToFile.pop();
                    writeToFile.push(n1);
                    writeToFile.push(n2);
                }
            }
        } else {
            writeToFile.push(":error:");
        }
    }


    private static void bind(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempStr20;
        String tempStr21;
        if (!writeToFile.isEmpty()) {
            String temp = writeToFile.peek();
            if (temp.charAt(0) == ':') {
                if (!(isBoolean(temp) || temp.equals(":unit:"))) {
                    writeToFile.push(":error:");
                    return;
                }
            }
        } else {
            writeToFile.push(":error:");
            return;
        }
        tempStr20 = writeToFile.pop();

        if (!writeToFile.isEmpty()) {
            tempStr21 = writeToFile.pop();
            var.put(tempStr21, tempStr20);
            writeToFile.push(":unit:");
            return;
        } else {
            if (!Character.isLetter(writeToFile.peek().charAt(0))) {
                writeToFile.push(tempStr20);
                writeToFile.push(":error:");
                return;
            }
        }
    }

    private static void cat(Stack<String> writeToFile, HashMap<String, String> var) {
        String str;
        String tempS19;
        String tempS20;
        tempS19 = isValid(writeToFile, var, false, true, true, false);
        tempS20 = isValid(writeToFile, var, false, true, true, false);
        if (tempS19 == null) {
            writeToFile.push(":error:");
            return;
        }

        if (tempS20 == null) {
            writeToFile.push(tempS19);
            writeToFile.push(":error:");
            return;
        }

        if (tempS19 != null && tempS20 != null) {
            if (var.containsKey(tempS19)) {
                tempS19 = var.get(tempS19);
                ;
            }

            if (var.containsKey(tempS20)) {
                tempS20 = var.get(tempS20);

            }
            str = tempS20 + tempS19;
            writeToFile.push(str.replace("\"", ""));
            return;
        }
    }

    private static void equal(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN15;
        String tempN16;

        if (var.isEmpty() && !writeToFile.isEmpty()) {

            tempN15 = writeToFile.pop();
            if (writeToFile.size() != 0) {
                tempN16 = writeToFile.pop();
                if (isNumeric(tempN15)) {
                    if (isNumeric(tempN16)) {
                        if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                            writeToFile.push(":true:");
                            return;
                        } else {
                            writeToFile.push(":false:");
                            return;
                        }

                    } else {
                        writeToFile.push(tempN16);
                        writeToFile.push(tempN15);
                        writeToFile.push(":error:1");
                        return;
                    }
                } else {
                    writeToFile.push(tempN16);
                    writeToFile.push(tempN15);
                    writeToFile.push(":error:2");
                    return;
                }
            } else {
                writeToFile.push(tempN15);
                writeToFile.push(":error:3");
                return;
            }
        }

        if (!(!var.isEmpty() && !writeToFile.isEmpty())) {
            writeToFile.push(":error:4");
        } else {
            tempN15 = isValid(writeToFile, var, true, true, false, false);
            if (tempN15 == null) {
                writeToFile.push(":error:5");
            } else {
                if (var.containsKey(tempN15)) {
                    tempN16 = isValid(writeToFile, var, true, true, false, false);
                    if (Character.isLetter(tempN16.charAt(0))) {
                        if (var.containsKey(tempN16)) {
                            tempN16 = var.get(tempN16);
                            if (isNumeric(tempN16)) {
                                if (var.containsKey(tempN15)) {
                                    tempN15 = var.get(tempN15);
                                    if (isNumeric(tempN15)) {
                                        if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                            writeToFile.push(":true:");
                                            return;
                                        } else {
                                            writeToFile.push(":false:");
                                            return;
                                        }
                                    }
                                }

                                if (var.containsKey(tempN15)) {
                                    tempN15 = var.get(tempN15);
                                    if (isNumeric(tempN15)) {

                                        if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                            writeToFile.push(":true:");
                                            return;
                                        } else {
                                            writeToFile.push(":false:");
                                            return;
                                        }
                                    }
                                }

                            } else {
                                writeToFile.push(tempN15);
                                writeToFile.push(":error:6");
                                return;
                            }
                        }
                        if (isNumeric(tempN16)) {
                            if (var.containsKey(tempN16)) {
                                if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                    writeToFile.push(":true:");
                                    return;
                                } else {
                                    writeToFile.push(":false:");
                                    return;
                                }
                            } else {
                                if (var.containsKey(tempN15)) {
                                    tempN15 = var.get(tempN15);
                                    if (isNumeric(tempN15)) {
                                        if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                            writeToFile.push(":true:");
                                            return;
                                        } else {
                                            writeToFile.push(":false:");
                                            return;
                                        }
                                    }
                                } else if (isNumeric(tempN15)) {
                                    if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                        writeToFile.push(":true:");
                                        return;
                                    } else {
                                        writeToFile.push(":false:");
                                        return;
                                    }
                                } else {
                                    writeToFile.push(tempN15);
                                    writeToFile.push(":error:7");
                                    return;
                                }

                            }
                        }
                        writeToFile.push(tempN15);
                        writeToFile.push(":error:8");
                        return;

                    } else {

                        if (var.containsKey(tempN15)) {
                            tempN15 = var.get(tempN15);
                        }

                        if (isNumeric(tempN16)) {
                            if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                writeToFile.push(":true:");
                                return;
                            } else {
                                writeToFile.push(":false:");
                                return;
                            }
                        }

                    }
                } else {

                    if (isNumeric(tempN15)) {
                        tempN16 = isValid(writeToFile, var, true, true, false, false);
                        if (tempN16 == null) {
                            writeToFile.push(tempN15);
                            writeToFile.push(":error:");
                            return;
                        }
                        if (var.containsKey(tempN16)) {
                            if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                writeToFile.push(":true:");
                                return;
                            } else {
                                writeToFile.push(":false:");
                                return;
                            }
                        } else {
                            if (isNumeric(tempN16)) {
                                if (Integer.parseInt(tempN16) == Integer.parseInt(tempN15)) {
                                    writeToFile.push(":true:");
                                    return;
                                } else {
                                    writeToFile.push(":false:");
                                    return;
                                }
                            } else {
                                writeToFile.push(tempN15);
                                writeToFile.push(":error:");
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void lessThan(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN15;
        String tempN16;

        if (var.isEmpty() && !writeToFile.isEmpty()) {

            tempN15 = writeToFile.pop();
            if (writeToFile.size() != 0) {
                tempN16 = writeToFile.pop();
                if (isNumeric(tempN15)) {
                    if (isNumeric(tempN16)) {
                        if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                            writeToFile.push(":true:");
                        } else {
                            writeToFile.push(":false:");
                        }

                    } else {
                        writeToFile.push(tempN16);
                        writeToFile.push(tempN15);
                        writeToFile.push(":error:");
                        return;
                    }
                } else {
                    writeToFile.push(tempN16);
                    writeToFile.push(tempN15);
                    writeToFile.push(":error:");
                    return;
                }
            } else {
                writeToFile.push(tempN15);
                writeToFile.push(":error:");
                return;
            }
        }

        if (!(!var.isEmpty() && !writeToFile.isEmpty())) {
            writeToFile.push(":error:");
        } else {
            tempN15 = isValid(writeToFile, var, true, true, false, false);
            if (tempN15 == null) {
                writeToFile.push(":error:");
            } else {
                if (var.containsKey(tempN15)) {
                    tempN16 = isValid(writeToFile, var, true, true, false, false);
                    if (Character.isLetter(tempN16.charAt(0))) {
                        if (var.containsKey(tempN16)) {
                            tempN16 = var.get(tempN16);
                            if (isNumeric(tempN16)) {
                                if (var.containsKey(tempN15)) {
                                    tempN15 = var.get(tempN15);
                                    if (isNumeric(tempN15)) {
                                        if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                            writeToFile.push(":true:");
                                        } else {
                                            writeToFile.push(":false:");
                                        }
                                    }
                                }

                                if (var.containsKey(tempN15)) {
                                    tempN15 = var.get(tempN15);
                                    if (isNumeric(tempN15)) {

                                        if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                            writeToFile.push(":true:");
                                        } else {
                                            writeToFile.push(":false:");
                                        }
                                    }
                                }

                            } else {
                                writeToFile.push(tempN15);
                                writeToFile.push(":error:");
                            }
                        }
                        if (isNumeric(tempN16)) {
                            if (var.containsKey(tempN16)) {
                                if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                    writeToFile.push(":true:");
                                } else {
                                    writeToFile.push(":false:");
                                }
                            } else {
                                if (var.containsKey(tempN15)) {
                                    tempN15 = var.get(tempN15);
                                    if (isNumeric(tempN15)) {
                                        if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                            writeToFile.push(":true:");
                                        } else {
                                            writeToFile.push(":false:");
                                        }
                                    }
                                } else if (isNumeric(tempN15)) {
                                    if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                        writeToFile.push(":true:");
                                    } else {
                                        writeToFile.push(":false:");
                                    }
                                    return;
                                } else {
                                    writeToFile.push(tempN15);
                                    writeToFile.push(":error:");
                                }

                            }
                        }
                        writeToFile.push(tempN15);
                        writeToFile.push(":error:");

                    } else {
                        writeToFile.push(tempN15);
                        writeToFile.push(":error:");
                    }
                } else {

                    if (isNumeric(tempN15)) {
                        tempN16 = isValid(writeToFile, var, true, true, false, false);
                        if (tempN16 == null) {
                            writeToFile.push(tempN15);
                            writeToFile.push(":error:");
                        }
                        if (var.containsKey(tempN16)) {
                            if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                writeToFile.push(":true:");
                            } else {
                                writeToFile.push(":false:");
                            }
                        } else {
                            if (isNumeric(tempN16)) {
                                if (Integer.parseInt(tempN16) < Integer.parseInt(tempN15)) {
                                    writeToFile.push(":true:");
                                } else {
                                    writeToFile.push(":false:");
                                }
                            } else {
                                writeToFile.push(tempN15);
                                writeToFile.push(":error:");
                            }
                        }
                    }
                }
            }
        }
    }

    private static void or(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempB12;
        String tempB13;

        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isBoolean(writeToFile.peek())) {
                tempB12 = writeToFile.pop();
                if (writeToFile.size() != 0) {
                    if (isBoolean(writeToFile.peek())) {
                        tempB13 = writeToFile.pop();
                        if (tempB12.equals(":true:") || tempB13.equals(":true:")) {
                            writeToFile.push(":true:");
                        } else {
                            writeToFile.push(":false:");
                        }
                    } else {
                        writeToFile.push(tempB12);
                        writeToFile.push(":error:");
                    }
                } else {
                    writeToFile.push(tempB12);
                    writeToFile.push(":error:");
                }
            } else {
                writeToFile.push(":error:");
            }
        }
        if (!var.isEmpty() && !writeToFile.isEmpty()) {
            tempB12 = isValid(writeToFile, var, false, true, false, true);
            if (var.containsKey(tempB12)) {
                if (isBoolean(tempB12)) {
                    if (var.size() != 1) {
                        tempB13 = isValid(writeToFile, var, false, true, false, true);
                        if (var.containsKey(tempB13)) {
                            if (isBoolean(tempB13)) {
                                if (tempB12.equals(":true:") || tempB13.equals(":true:")) {
                                    writeToFile.push(":true:");
                                    return;

                                } else {
                                    writeToFile.push(":false:");
                                    return;

                                }
                            } else {
                                writeToFile.push(tempB13);
                                writeToFile.push(tempB12);
                                writeToFile.push(":error:");
                                return;

                            }
                        } else {
                            writeToFile.push(tempB13);
                            writeToFile.push(tempB12);
                            writeToFile.push(":error:");
                            return;

                        }
                    } else {
                        writeToFile.push(tempB12);
                        writeToFile.push(":error:");
                        return;

                    }
                } else {
                    writeToFile.push(tempB12);
                    writeToFile.push(":error:");
                    return;

                }
            } else {
                writeToFile.push(tempB12);
                writeToFile.push(":error:");
                return;

            }
        } else {
            writeToFile.push(":error:");
            return;
        }

    }

    private static void not(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempB14;
        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isBoolean(writeToFile.peek())) {
                tempB14 = writeToFile.pop();
                if (tempB14.equals(":true:")) {
                    writeToFile.push(":false:");
                } else {
                    writeToFile.push(":true:");
                }
            } else {
                writeToFile.push(":error:");
            }
        }
        if (!var.isEmpty() && !writeToFile.isEmpty()) {
            tempB14 = isValid(writeToFile, var, false, true, false, true);
            if (var.containsKey(tempB14) && tempB14 != null) {
                if (isBoolean(tempB14)) {
                    if (tempB14.equals(":true:")) {
                        writeToFile.push(":false:");
                    } else {
                        writeToFile.push(":true:");
                    }
                } else {
                    tempB14 = var.get(tempB14);
                    if (isBoolean(tempB14)) {
                        if (tempB14.equals(":true:")) {
                            writeToFile.push(":false:");
                        } else {
                            writeToFile.push(":true:");
                        }
                    }
                }
            } else {
                writeToFile.push(tempB14);
                writeToFile.push(":error:");
            }
        }
    }

    private static void and(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempB10;
        String tempB11;

        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isBoolean(writeToFile.peek())) {
                tempB10 = writeToFile.pop();
                if (writeToFile.size() != 0) {
                    if (isBoolean(writeToFile.peek())) {
                        tempB11 = writeToFile.pop();
                        if (tempB10.equals(":false:") || tempB11.equals(":false:")) {
                            writeToFile.push(":false:");
                            return;
                        } else {
                            writeToFile.push(":true:");
                            return;

                        }
                    } else {
                        writeToFile.push(tempB10);
                        writeToFile.push(":error:");
                        return;

                    }
                } else {
                    writeToFile.push(tempB10);
                    writeToFile.push(":error:");
                    return;
                }
            } else {
                writeToFile.push(":error:");
                return;
            }
        }
        if (!var.isEmpty() && !writeToFile.isEmpty()) {
            tempB10 = isValid(writeToFile, var, false, true, false, true);
            if (var.containsKey(tempB10)) {
                if (isBoolean(tempB10)) {
                    if (var.size() != 1) {
                        tempB11 = isValid(writeToFile, var, false, true, false, true);
                        if (var.containsKey(tempB11)) {
                            if (isBoolean(tempB11)) {
                                if (tempB10.equals(":false:") || tempB11.equals(":false:")) {
                                    writeToFile.push(":false:");
                                    return;

                                } else {
                                    writeToFile.push(":true:");
                                    return;

                                }
                            } else {
                                writeToFile.push(tempB11);
                                writeToFile.push(tempB10);
                                writeToFile.push(":error:");
                                return;

                            }
                        } else {
                            writeToFile.push(tempB11);
                            writeToFile.push(tempB10);
                            writeToFile.push(":error:");
                            return;

                        }
                    } else {
                        writeToFile.push(tempB10);
                        writeToFile.push(":error:");
                        return;

                    }
                } else {
                    writeToFile.push(tempB10);
                    writeToFile.push(":error:");
                    return;

                }
            } else {
                writeToFile.push(tempB10);
                writeToFile.push(":error:");
                return;

            }
        } else {
            writeToFile.push(":error:");
            return;
        }
    }

    private static void neg(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN9;
        if (var.isEmpty() && !writeToFile.isEmpty()) {
            tempN9 = writeToFile.pop();
            if (isNumeric(tempN9)) {
                if (Integer.parseInt(tempN9) == 0) {
                    writeToFile.push(Integer.toString(Integer.parseInt(tempN9)));
                } else {
                    writeToFile.push(Integer.toString(-Integer.parseInt(tempN9)));
                }
            } else {
                writeToFile.push(tempN9);
                writeToFile.push(":error:");
            }
        }

        if (!var.isEmpty() && !writeToFile.isEmpty()) {
            tempN9 = isValid(writeToFile, var, true, true, false, false);
            if (tempN9 == null) {
                writeToFile.push(":error:");
            } else {
                if (var.containsKey(tempN9)) {
                    tempN9 = var.get(tempN9);
                    if (isNumeric(tempN9)) {
                        if (Integer.parseInt(tempN9) == 0) {
                            writeToFile.push(Integer.toString(Integer.parseInt(tempN9)));
                        } else {
                            writeToFile.push(Integer.toString(-Integer.parseInt(tempN9)));
                        }
                    } else {
                        writeToFile.push(tempN9);
                        writeToFile.push(":error:");
                    }
                } else {
                    writeToFile.push(tempN9);
                    writeToFile.push(":error:");
                }
            }
        }
    }

    private static void pop(Stack<String> writeToFile) {
        if (!writeToFile.isEmpty())
            writeToFile.pop();
        else {
            writeToFile.push(":error:");
        }
    }

    private static void div(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN7;
        String tempN8;
        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isNumeric(writeToFile.peek())) {
                tempN7 = writeToFile.pop();
                if (Integer.parseInt(tempN7) == 0) {
                    writeToFile.push(tempN7);
                    writeToFile.push(":error:");
                    return;
                } else {
                    if (isNumeric(writeToFile.peek())) {
                        tempN8 = writeToFile.pop();
                        writeToFile.push(Integer.toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                        return;
                    } else {
                        writeToFile.push(tempN7);
                        writeToFile.push(":error:");
                        return;
                    }
                }
            } else {
                writeToFile.push(":error:");
                return;
            }
        } else if (!var.isEmpty()) {
            if (writeToFile.isEmpty()) {
                writeToFile.push(":error:");
                return;
            } else {
                tempN7 = isValid(writeToFile, var, true, true, false, false);
                if (tempN7 == null) {
                    writeToFile.push(":error:");
                    return;
                } else {
                    if (var.containsKey(tempN7)) {
                        tempN8 = isValid(writeToFile, var, true, true, false, false);
                        if (Character.isLetter(tempN8.charAt(0))) {
                            if (var.containsKey(tempN8)) {
                                tempN8 = var.get(tempN8);
                                if (isNumeric(tempN8)) {
                                    if (var.containsKey(tempN7)) {
                                        tempN7 = var.get(tempN7);
                                        if (isNumeric(tempN7)) {
                                            if (Integer.parseInt(tempN7) == 0) {
                                                writeToFile.push(tempN8);
                                                writeToFile.push(tempN7);
                                                writeToFile.push(":error:");
                                                return;
                                            } else {
                                                writeToFile.push(Integer
                                                        .toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                                return;
                                            }
                                        }
                                    }

                                    if (var.containsKey(tempN7)) {
                                        tempN7 = var.get(tempN7);
                                        if (isNumeric(tempN7)) {
                                            if (Integer.parseInt(tempN7) == 0) {
                                                writeToFile.push(tempN8);
                                                writeToFile.push(tempN7);
                                                writeToFile.push(":error:");
                                                return;
                                            } else {
                                                writeToFile.push(Integer
                                                        .toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                                return;
                                            }
                                        }
                                    }

                                } else {
                                    writeToFile.push(tempN7);
                                    writeToFile.push(":error:");
                                    return;
                                }
                            }
                            if (isNumeric(tempN8)) {
                                if (var.containsKey(tempN8)) {
                                    if (Integer.parseInt(tempN7) == 0) {
                                        writeToFile.push(tempN8);
                                        writeToFile.push(tempN7);
                                        writeToFile.push(":error:");
                                        return;
                                    } else {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                        return;
                                    }
                                } else {
                                    if (var.containsKey(tempN7)) {
                                        tempN7 = var.get(tempN7);
                                        if (isNumeric(tempN7)) {
                                            if (Integer.parseInt(tempN7) == 0) {
                                                writeToFile.push(tempN8);
                                                writeToFile.push(tempN7);
                                                writeToFile.push(":error:");
                                                return;
                                            } else {
                                                writeToFile.push(Integer
                                                        .toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                                return;
                                            }
                                        }
                                    } else if (isNumeric(tempN7)) {
                                        if (Integer.parseInt(tempN7) == 0) {
                                            writeToFile.push(tempN8);
                                            writeToFile.push(tempN7);
                                            writeToFile.push(":error:");
                                            return;
                                        } else {
                                            writeToFile.push(Integer
                                                    .toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                            return;
                                        }

                                    } else {
                                        writeToFile.push(tempN7);
                                        writeToFile.push(":error:");
                                        return;
                                    }

                                }
                            }

                            writeToFile.push(tempN7);
                            writeToFile.push(":error:");
                            return;

                        } else {
                            writeToFile.push(tempN7);
                            writeToFile.push(":error:");
                        }
                    } else {

                        if (isNumeric(tempN7)) {
                            tempN8 = isValid(writeToFile, var, true, true, false, false);
                            if (tempN8 == null) {
                                writeToFile.push(tempN7);
                                writeToFile.push(":error:");
                                return;
                            }
                            if (var.containsKey(tempN8)) {
                                if (Integer.parseInt(tempN7) == 0) {
                                    writeToFile.push(tempN8);
                                    writeToFile.push(tempN7);
                                    writeToFile.push(":error:");
                                    return;
                                } else {
                                    writeToFile.push(Integer.toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                    return;
                                }
                            } else {
                                if (isNumeric(tempN8)) {
                                    if (Integer.parseInt(tempN7) == 0) {
                                        writeToFile.push(tempN8);
                                        writeToFile.push(tempN7);
                                        writeToFile.push(":error:");
                                        return;
                                    } else {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                                        return;
                                    }
                                } else {
                                    writeToFile.push(tempN7);
                                    writeToFile.push(":error:");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void add(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN1;
        String tempN2;
        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isString(writeToFile.peek())) {
                tempN1 = writeToFile.pop();
                if (isString(writeToFile.peek()) && !writeToFile.isEmpty()) {
                    tempN2 = writeToFile.pop();
                    writeToFile.push(Integer.toString(Integer.parseInt(tempN1) + Integer.parseInt(tempN2)));
                    return;
                } else {
                    writeToFile.push(tempN1);
                    writeToFile.push(":error:");
                    return;
                }
            } else {
                writeToFile.push(":error:");

                return;
            }
        }

        if (writeToFile.isEmpty()) {
            writeToFile.push(":error:");
            return;
        } else {
            tempN1 = isValid(writeToFile, var, true, true, false, false);
            if (tempN1 == null) {
                writeToFile.push(":error:");
                return;
            } else {
                if (var.containsKey(tempN1)) {
                    tempN2 = isValid(writeToFile, var, true, true, false, false);
                    if (Character.isLetter(tempN2.charAt(0))) {
                        if (var.containsKey(tempN2)) {
                            tempN2 = var.get(tempN2);
                            if (isNumeric(tempN2)) {
                                if (var.containsKey(tempN1)) {
                                    tempN1 = var.get(tempN1);
                                    if (isNumeric(tempN1)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN1) + Integer.parseInt(tempN2)));
                                        return;
                                    }
                                }

                                if (var.containsKey(tempN1)) {
                                    tempN1 = var.get(tempN1);
                                    if (isNumeric(tempN1)) {

                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN1) + Integer.parseInt(tempN2)));
                                        return;
                                    }
                                }

                            } else {
                                writeToFile.push(tempN1);
                                writeToFile.push(":error:");
                                return;
                            }
                        }
                        if (isNumeric(tempN2)) {
                            if (var.containsKey(tempN2)) {
                                writeToFile.push(Integer
                                        .toString(Integer.parseInt(tempN1) + Integer.parseInt(var.get(tempN2))));
                                return;
                            } else {
                                if (var.containsKey(tempN1)) {
                                    tempN1 = var.get(tempN1);
                                    if (isNumeric(tempN1)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN1) + Integer.parseInt(tempN2)));
                                        return;
                                    }
                                } else if (isNumeric(tempN1)) {
                                    writeToFile.push(Integer.toString(Integer.parseInt(tempN1) + Integer.parseInt(tempN2)));
                                    return;
                                } else {
                                    writeToFile.push(tempN1);
                                    writeToFile.push(":error:");
                                    return;
                                }

                            }
                        }
                        writeToFile.push(tempN1);
                        writeToFile.push(":error:");
                        return;
                    } else {
                        writeToFile.push(tempN1);
                        writeToFile.push(":error:");
                        return;
                    }
                } else {

                    if (isNumeric(tempN1)) {
                        tempN2 = isValid(writeToFile, var, true, true, false, false);
                        if (tempN2 == null) {
                            writeToFile.push(tempN1);
                            writeToFile.push(":error:");
                            return;
                        }
                        if (var.containsKey(tempN2)) {
                            writeToFile.push(Integer
                                    .toString(Integer.parseInt(tempN1) + Integer.parseInt(var.get(tempN2))));
                            return;
                        } else {
                            if (isNumeric(tempN2)) {
                                int x = Integer.parseInt(tempN1);
                                int y = Integer.parseInt(tempN2);
                                int z = x + y;
                                writeToFile.push(Integer.toString(z));
                                return;
                            } else {
                                writeToFile.push(tempN1);
                                writeToFile.push(":error:");
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

    private static void sub(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN3;
        String tempN4;
        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isNumeric(writeToFile.peek())) {
                tempN3 = writeToFile.pop();
                if (isNumeric(writeToFile.peek()) && !writeToFile.isEmpty()) {
                    tempN4 = writeToFile.pop();
                    writeToFile.push(Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));
                    return;
                } else {
                    writeToFile.push(tempN3);
                    writeToFile.push(":error:");
                    return;
                }
            } else {
                writeToFile.push(":error:");
                return;
            }
        }
        if (writeToFile.isEmpty()) {
            writeToFile.push(":error:");
            return;
        } else {
            tempN3 = isValid(writeToFile, var, true, true, false, false);
            if (tempN3 == null) {
                writeToFile.push(":error:");
                return;
            } else {
                if (var.containsKey(tempN3)) {
                    tempN4 = isValid(writeToFile, var, true, true, false, false);
                    if (Character.isLetter(tempN4.charAt(0))) {
                        if (var.containsKey(tempN4)) {
                            tempN4 = var.get(tempN4);
                            if (isNumeric(tempN4)) {
                                if (var.containsKey(tempN3)) {
                                    tempN3 = var.get(tempN3);
                                    if (isNumeric(tempN3)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                                        return;
                                    }
                                }

                                if (var.containsKey(tempN3)) {
                                    tempN3 = var.get(tempN3);
                                    if (isNumeric(tempN3)) {

                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                                        return;
                                    }
                                }

                            } else {
                                writeToFile.push(tempN3);
                                writeToFile.push(":error:");

                                return;
                            }
                        }
                        if (isNumeric(tempN4)) {
                            if (var.containsKey(tempN4)) {
                                writeToFile.push(Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                                return;
                            } else {
                                if (var.containsKey(tempN3)) {
                                    tempN3 = var.get(tempN3);
                                    if (isNumeric(tempN3)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                                        return;
                                    }
                                } else if (isNumeric(tempN3)) {

                                    writeToFile.push(Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                                    return;
                                } else {
                                    writeToFile.push(tempN3);
                                    writeToFile.push(":error:");
                                    return;
                                }

                            }
                        }

                        writeToFile.push(tempN3);
                        writeToFile.push(":error:");

                        return;
                    } else {
                        writeToFile.push(tempN3);
                        writeToFile.push(":error:");
                        return;
                    }
                } else {

                    if (isNumeric(tempN3)) {
                        tempN4 = isValid(writeToFile, var, true, true, false, false);
                        if (tempN4 == null) {
                            writeToFile.push(tempN3);
                            writeToFile.push(":error:");

                            return;
                        }
                        if (var.containsKey(tempN4)) {
                            tempN4 = var.get(tempN4);
                            writeToFile.push(Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                            return;
                        } else {
                            if (isNumeric(tempN4)) {
                                writeToFile.push(Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));

                                return;
                            } else {
                                writeToFile.push(tempN3);
                                writeToFile.push(":error:");

                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void mul(Stack<String> writeToFile, HashMap<String, String> var) {
        String tempN5;
        String tempN6;
        if (var.isEmpty() && !writeToFile.isEmpty()) {
            if (isNumeric(writeToFile.peek())) {
                tempN5 = writeToFile.pop();
                if (isNumeric(writeToFile.peek()) && !writeToFile.isEmpty()) {
                    tempN6 = writeToFile.pop();
                    writeToFile.push(Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));

                    return;
                } else {
                    writeToFile.push(tempN5);
                    writeToFile.push(":error:");

                    return;
                }
            } else {
                writeToFile.push(":error:");

                return;
            }
        }
        if (writeToFile.isEmpty()) {
            writeToFile.push(":error:");
            return;
        } else {
            tempN5 = isValid(writeToFile, var, true, true, false, false);
            if (tempN5 == null) {
                writeToFile.push(":error:");
                return;
            } else {
                if (var.containsKey(tempN5)) {
                    tempN6 = isValid(writeToFile, var, true, true, false, false);
                    if (Character.isLetter(tempN6.charAt(0))) {
                        if (var.containsKey(tempN6)) {
                            tempN6 = var.get(tempN6);
                            if (isNumeric(tempN6)) {
                                if (var.containsKey(tempN5)) {
                                    tempN5 = var.get(tempN5);
                                    if (isNumeric(tempN5)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                                        return;
                                    }
                                }

                                if (var.containsKey(tempN5)) {
                                    tempN5 = var.get(tempN5);
                                    if (isNumeric(tempN5)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                                    }
                                }

                            } else {
                                writeToFile.push(tempN5);
                                writeToFile.push(":error:");
                                return;
                            }
                        }
                        if (isNumeric(tempN6)) {
                            if (var.containsKey(tempN6)) {
                                writeToFile.push(Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                            } else {
                                if (var.containsKey(tempN5)) {
                                    tempN5 = var.get(tempN5);
                                    if (isNumeric(tempN5)) {
                                        writeToFile.push(
                                                Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                                        return;
                                    }
                                } else if (isNumeric(tempN5)) {

                                    writeToFile.push(Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                                    writeToFile.pop();
                                    return;
                                } else {
                                    writeToFile.push(tempN5);
                                    writeToFile.push(":error:");
                                    return;
                                }

                            }
                        }

                        writeToFile.push(tempN5);
                        writeToFile.push(":error:");
                        return;

                    } else {

                        if (isNumeric(tempN6)) {
                            if (!isNumeric(tempN5)) {
                                if (var.containsKey(tempN5)) {
                                    tempN5 = var.get(tempN5);
                                    writeToFile.push(Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                                }
                            }

                            return;
                        }

                    }
                } else {

                    if (isNumeric(tempN5)) {
                        tempN6 = isValid(writeToFile, var, true, true, false, false);
                        if (tempN6 == null) {
                            writeToFile.push(tempN5);
                            writeToFile.push(":error:");
                            return;
                        }
                        if (var.containsKey(tempN6)) {
                            tempN6 = var.get(tempN6);
                            writeToFile.push(Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                            return;
                        } else {
                            if (isNumeric(tempN6)) {
                                writeToFile.push(Integer.toString(Integer.parseInt(tempN6) * Integer.parseInt(tempN5)));
                                return;
                            } else {
                                writeToFile.push(tempN5);
                                writeToFile.push(":error:");
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private static String isValid(Stack<String> writeToFile, HashMap<String, String> var, boolean isNum, boolean isVar,
                                  boolean isStr, boolean isBool) {
        String tempStr0;
        if (!writeToFile.isEmpty()) {
            tempStr0 = writeToFile.peek();
            if (!isNull(tempStr0)) {
                if (isVar) {
                    if (foundEle(writeToFile, var, tempStr0)) {
                        tempStr0 = var.get(tempStr0);
                        if (!isNull(ret(writeToFile, isNum, isStr, isBool, tempStr0))) {
                            return writeToFile.pop();
                        } else {
                            return null;

                        }

                    } else {
                        if (isNull(ret(writeToFile, isNum, isStr, isBool, tempStr0))) {
                            return null;
                        } else {
                            return writeToFile.pop();
                        }
                    }
                }
            }
        }
        return null;
    }

    private static Integer ret(Stack<String> result, boolean isNum, boolean isStr, boolean isBool, String check) {
        if (!result.isEmpty()) {
            if (isNum && !isNumeric(check) || isStr && !isString(check) || isBool && !isBoolean(check)) {
                return null;
            }
        }
        return 0;
    }

    private static boolean foundEle(Stack<String> result, HashMap<String, String> var, String str) {
        if (str.length() == 0) {
            return var.containsKey(result.peek());
        } else {
            return var.containsKey(str);
        }
    }

    private static boolean isNull(Object obj) {
        return obj == null;
    }

    private static class Helper {
        private ArrayList<String> cmds = new ArrayList<>();
        private HashMap<String, String> var;
        private String fn;
        private int pos;
        private boolean isIn;
        private List<String> an;

        public Helper(String name, List<String> arg, HashMap<String, String> var, boolean isIn) {
            this.fn = name;
            this.an = arg;
            if (isIn) {
                this.var = var;
            } else {
                this.var = new HashMap<>(var);
            }
            this.pos = 0;
            this.isIn = isIn;
        }

        public static Helper valueOf(Object str) {
            Helper x = (Helper) str;
            return x;
        }

        public Helper(Helper e) {
            this(e.fn, e.an, e.var, e.isIn);
            this.cmds = e.cmds;
            this.pos = 0;
        }

        public void addCommand(String cmd) {
            cmds.add(cmd);
        }

        public HashMap<String, String> addE(List<String> arg) {
            if (isIn) {
                var = new HashMap<>(var);
            }
            int i = 0;
            while (i < arg.size()) {
                var.put(an.get(i), arg.get(i));
                i++;
            }
            return var;
        }

        public List<String> getArgName() {
            return an;
        }

        public ArrayList<String> getCmds() {
            return cmds;
        }

        public void setCmds(ArrayList<String> cmds) {
            this.cmds = cmds;
        }

        public HashMap<String, String> getVar() {
            return var;
        }

        public void setVar(HashMap<String, String> var) {
            this.var = var;
        }

        public String getFn() {
            return fn;
        }

        public void setFn(String fn) {
            this.fn = fn;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public boolean isIn() {
            return isIn;
        }

        public void setIn(boolean isIn) {
            this.isIn = isIn;
        }

        public List<String> getAn() {
            return an;
        }

        public void setAn(List<String> an) {
            this.an = an;
        }

        public String readLine() {
            return cmds.get(pos++);
        }

    }

    private static boolean isInhere(HashMap<String, String> var, HashMap<String, Helper> tf, String arg) {
        boolean tempBool = false;
        if (!isBoolean(arg)) {
            if (!isNumeric(arg)) {
                if (!isString(arg)) {
                    if (!arg.equals(":unit:")) {
                        if (!var.containsKey(arg)) {
                            if (!tf.containsKey(arg)) {
                                tempBool = true;
                            }
                        }
                    }
                }
            }
        }
        return tempBool;
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isString(String str) {
        return str.startsWith("\"") && str.endsWith("\"");
    }

    private static boolean isBoolean(String str) {
        return str.equals(":true:") || str.equals(":false:");
    }
}