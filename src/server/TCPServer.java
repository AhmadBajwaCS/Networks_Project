package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {

    public static void main(String argv[]) throws Exception
    {
        String clientSentence;
        String result;

        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true) {

            Socket connectionSocket = welcomeSocket.accept();

            BufferedReader inFromClient =
                    new BufferedReader(new
                            InputStreamReader(connectionSocket.getInputStream()));

            DataOutputStream  outToClient =
                    new DataOutputStream(connectionSocket.getOutputStream());

            // get client input, calculate result, and output to client. If client requests
            //  to close the connection, close instead
            clientSentence = inFromClient.readLine();
            if (clientSentence.toLowerCase().equals("close")) {
                connectionSocket.close();
            }
            else {
                result = evaluate(clientSentence) + '\n';
                outToClient.writeBytes(result);
            }
        }
    }

    private static int getPrecedence(char operator)
    {
        // return -1 if character is not an operator
        switch(operator) {
            case '+': case '-':
                return 1;
            case '*': case '/':
                return 2;
            case '^':
                return 3;
            default:
                return -1;
        }
    }

    private static List<String> infixToPostfix(String expression)
    {
        List<String> postfixExpression = new ArrayList<>();
        Stack<Character> charStack = new Stack<>();

        String nextNum = "";
        char c;

        for (int i = 0; i < expression.length(); ++i) {
            c = expression.charAt(i);

            if (Character.isDigit(c)) {
                nextNum += c;
            }
            
            else if (c == '(')
                charStack.push(c);
            
            else if (c == ')') {
                while (!charStack.isEmpty() && charStack.peek() != '(') {
                    postfixExpression.add(Character.toString(charStack.peek()));
                    charStack.pop();
                }
                charStack.pop();
            }
            // token is an operator
            else {
                postfixExpression.add(nextNum);
                nextNum = "";
                while (!charStack.isEmpty() && getPrecedence(c) < getPrecedence(charStack.peek())) {
                    postfixExpression.add(Character.toString(charStack.peek()));
                    charStack.pop();
                }
                charStack.push(c);
            }
        }

        if (!nextNum.equals(""))
            postfixExpression.add(nextNum);
        while (!charStack.isEmpty()) {
            if (charStack.peek() == '(')
                return null;
            postfixExpression.add(Character.toString(charStack.peek()));
            charStack.pop();
        }

        return postfixExpression;
    }

    private static String evaluate(String expression)
    {
        String resultSentence = "";
        expression = expression.replaceAll("\\s", "");
        List<String> postfixExpression = infixToPostfix(expression);

        if (postfixExpression == null) {
            resultSentence = "ERROR: Invalid expression entered. Please try again.";
            return resultSentence;
        }

        Stack<Integer> expressionStack = new Stack<>();

        int operand1, operand2;
        for (int i = 0; i < postfixExpression.size(); ++i) {
            String s = postfixExpression.get(i);
            if (s.matches("[+-//*]")) {
                operand1 = expressionStack.pop();
                operand2 = expressionStack.pop();
                int result = 0;
                switch(s) {
                    case "+":
                        result = (operand2 + operand1);
                        break;
                    case "-":
                        result = (operand2 - operand1);
                        break;
                    case "*":
                        result = (operand2 * operand1);
                        break;
                    case "/":
                        // divide by 0 error
                        if (operand1 == 0) {
                            resultSentence = "ERROR: Divide by 0.";
                            return resultSentence;
                        }
                        result = (operand2 / operand1);
                        break;
                }
                expressionStack.push(result);
            }
            else {
                expressionStack.push(Integer.parseInt(s));
            }
        }

        resultSentence = Integer.toString(expressionStack.pop());
        return resultSentence;
    }

    private static void logInfo()
    {
        String filename = "log.tx";
        FileWriter logWriter;
        try {
            File file = new File(filename);
        }
        catch (IOException e)
        {
            System.out.println("Log file not found. Creating new file");
            File logfile = new File("serverLog.txt");
        }

    }
}
