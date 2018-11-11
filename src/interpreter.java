
import java.util.*;
import java.io.*;


public class interpreter {

    public static void interpreter(String input, String output) throws IOException {
        Stack<String> info = new Stack<String>();
        ArrayList<Stack<String>> writeFile = new ArrayList<Stack<String>>();
        writeFile.add(info);

        try (BufferedReader br = new BufferedReader((new FileReader(input)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p1 = line.split("\\s+");
                if (p1[0].equals("push"))
                {
                    String tempStr = line.substring(p1[0].length()+1);
                    if (!(p1.length <= 1)) // check this part
                    {   // check zero
                        if (tempStr.equals("0") || tempStr.equals("-0"))
                        {
                            writeFile.get(0).push("0");
                        }
                        // check positive number
                        else if (tempStr.matches("[0-9]+"))
                        {
                            writeFile.get(0).push(tempStr);
                        }
                        // check string
                        else if(tempStr.startsWith("\"") && tempStr.endsWith("\""))
                        {
                            writeFile.get(0).push(tempStr.substring(1,tempStr.length()-1));
                        }
                        // check variable name
                        else if(tempStr.charAt(0) == '_' ||Character.isLetter(tempStr.charAt(0)) && (!tempStr.contains(",") && !tempStr.contains(" ")))
                        {
                            writeFile.get(0).push(tempStr);
                        }
                        // check boolean and error
                        else if(tempStr.contains(":true:") ||(tempStr.contains(":false:")||tempStr.contains(":error:")))
                        {
                            writeFile.get(0).push(tempStr);
                        }
                        // check negative number and prevent negative number
                        else if (tempStr.charAt(0) == ('-') && Character.isDigit(tempStr.charAt(1)))
                        {
                            boolean tempBool = false;
                            for (char x : tempStr.toCharArray())
                            {
                                if (x == '.')
                                {
                                    tempBool = true;
                                }
                            }
                            if (tempBool)
                            {
                                writeFile.get(0).push(":error:");
                            }
                            else
                            {
                                writeFile.get(0).push(tempStr);
                            }
                        }
                        else
                        {
                            writeFile.get(0).push(":error:");
                        }
                    }


                } else if (p1[0].equals("pop")) {
                    if (writeFile.get(0).size() != 0) {
                        writeFile.get(0).pop();
                    } else {
                        writeFile.get(0).push(":error:");
                    }
                } else if (p1[0].equals("add")) {
                    String tempN1;
                    String tempN2;

                    if (writeFile.get(0).isEmpty() || !isNum(writeFile.get(0).peek())) {
                        writeFile.get(0).push(":error:");
                    } else {
                        tempN1 = writeFile.get(0).pop();

                        if (!isNum(writeFile.get(0).peek()) || writeFile.get(0).isEmpty()) {
                            writeFile.get(0).push(tempN1);
                            writeFile.get(0).push(":error:");
                        } else {
                            tempN2 = writeFile.get(0).pop();
                            writeFile.get(0).push(Integer.toString(Integer.parseInt(tempN1) + Integer.parseInt(tempN2)));
                        }
                    }
                } else if (p1[0].equals("sub")) {
                    String tempN3;
                    String tempN4;

                    if (writeFile.get(0).isEmpty() || writeFile.get(0).size() == 1 || !isNum(writeFile.get(0).peek())) {
                        writeFile.get(0).push(":error:");
                    } else {
                        tempN3 = writeFile.get(0).pop();
                        if (!isNum(writeFile.get(0).peek()) || writeFile.get(0).isEmpty()) {
                            writeFile.get(0).push(tempN3);
                            writeFile.get(0).push(":error:");
                        } else {
                            tempN4 = writeFile.get(0).pop();
                            writeFile.get(0).push(Integer.toString(Integer.parseInt(tempN4) - Integer.parseInt(tempN3)));
                        }
                    }
                } else if (p1[0].equals("mul")) {
                    String tempN5;
                    String tempN6;
                    if (writeFile.get(0).isEmpty() || writeFile.get(0).size() == 1 || !isNum(writeFile.get(0).peek())) {
                        writeFile.get(0).push(":error:");
                    } else {
                        tempN5 = writeFile.get(0).pop();
                        if (writeFile.get(0).isEmpty() || !isNum(writeFile.get(0).peek())) {
                            writeFile.get(0).push(tempN5);
                            writeFile.get(0).push(":error:");
                        } else {
                            tempN6 = writeFile.get(0).pop();
                            writeFile.get(0).push(Integer.toString(Integer.parseInt(tempN5) * Integer.parseInt(tempN6)));
                        }
                    }
                } else if (p1[0].equals("div")) {
                    String tempN7;
                    String tempN8;
                    if (writeFile.get(0).isEmpty() || writeFile.get(0).size() == 1 || !isNum(writeFile.get(0).peek())) {
                        writeFile.get(0).push(":error:");
                    } else {
                        tempN7 = writeFile.get(0).pop();
                        if (!isNum(writeFile.get(0).peek()) || writeFile.get(0).isEmpty()) {
                            writeFile.get(0).push(tempN7);
                            writeFile.get(0).push(":error:");
                        } else {
                            tempN8 = writeFile.get(0).pop();
                            if (tempN7.equals("0")) {
                                writeFile.get(0).push(tempN8);
                                writeFile.get(0).push(tempN7);
                                writeFile.get(0).push(":error:");
                            } else {
                                writeFile.get(0).push(Integer.toString(Integer.parseInt(tempN8) / Integer.parseInt(tempN7)));
                            }
                        }
                    }
                } else if (p1[0].equals("rem")) {
                    String tempN9;
                    String tempN10;
                    if (writeFile.get(0).isEmpty() || writeFile.get(0).size() == 1 || !isNum(writeFile.get(0).peek())) {
                        writeFile.get(0).push(":error:");
                    } else {
                        tempN9 = writeFile.get(0).pop();
                        if (!isNum(writeFile.get(0).peek())) {
                            writeFile.get(0).push(tempN9);
                            writeFile.get(0).push(":error:");
                        } else {
                            tempN10 = writeFile.get(0).pop();
                            if (tempN9.equals("0")) {
                                writeFile.get(0).push(tempN10);
                                writeFile.get(0).push(tempN9);
                                writeFile.get(0).push(":error:");
                            } else {
                                writeFile.get(0).push(Integer.toString((Integer.parseInt(tempN10) % Integer.parseInt(tempN9))));
                            }
                        }
                    }
                } else if (p1[0].equals("neg")) {
                    if (writeFile.get(0).isEmpty() || !isNum(writeFile.get(0).peek())) {
                        writeFile.get(0).push(":error:");
                    } else {
                        Integer tempStr11 = -Integer.parseInt(writeFile.get(0).pop());
                        writeFile.get(0).push(Integer.toString(tempStr11));
                    }
                } else if (p1[0].equals("swap")) {
                    String tempStr12;
                    String tempStr13;

                    if (writeFile.get(0).size() < 2 || writeFile.get(0).isEmpty()) {
                        writeFile.get(0).push(":error:");
                    } else {
                        tempStr12 = writeFile.get(0).pop();
                        if (writeFile.get(0).isEmpty()) {
                            writeFile.get(0).push(tempStr12);
                            writeFile.get(0).push(":error:");
                        } else {
                            tempStr13 = writeFile.get(0).pop();
                            writeFile.get(0).push(tempStr12);
                            writeFile.get(0).push(tempStr13);
                        }
                    }
                } else if (p1[0].equals("quit")) {
                    int tempNum = writeFile.get(0).size();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(output))));
                    for (int i = 0; i < tempNum; i++) {
                        bw.write(writeFile.get(0).pop());
                        if (!(i == tempNum - 1)) {
                            bw.newLine();
                        }
                    }
                    bw.close();
                } else {
                    System.out.println("Command code cannot be found, check the input file");
                }
            }
        }

    }


    private static boolean isNum(String tempStr)
    {
        try
        {
            Integer.parseInt(tempStr);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static void main(String[] args)
    {
        try
        {
            interpreter part1 = new interpreter();
            part1.interpreter("input.txt","output.text");
            //part1.interpreter("/Users/ericwang/Desktop/Interpret/src/example/input.txt","/Users/ericwang/Desktop/Interpret/src/example/output.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


