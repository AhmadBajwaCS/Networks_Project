package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ServerThread extends Thread {

    final Socket socket;
    final BufferedReader input;
    final DataOutputStream output;
    String username;
    Instant logonTime;
    Instant logoffTime;

    public ServerThread(Socket s, BufferedReader dis, DataOutputStream dos)
    {
        this.socket = s;
        this.input = dis;
        this.output = dos;
    }

    private boolean login()
    {
        try {
            username = this.input.readLine();
            this.output.writeBytes("Welcome, " + username + "! Type the command (close) at any time to disconnect.\n");
            logonTime = Instant.now();
            logInfo("New Connection: " + username + ", Address:" + socket.getLocalAddress() + ", Port: "
                    + socket.getPort() + ", Time: " + logonTime);
            logonTime = Instant.now();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run()
    {
        if (!login()) {
            return;
        }

        while (this.socket.isConnected()) {
            try {
                // get client input, calculate result, and output to client. If client requests
                //  to close the connection, close instead
                String clientSentence = this.input.readLine();
                if (clientSentence.toLowerCase().equals("close")) {
                    this.output.writeBytes("Disconnecting\n");
                    logoffTime = Instant.now();
                    logInfo("Connection closed: " + username + ", Address:" + socket.getLocalAddress() + ", Port: "
                            + socket.getPort() + ", Time: " + logoffTime + ", Total Time: " + Duration.between(logonTime, logoffTime));
                    this.socket.close();
                    break;
                }
                else {
                    logInfo("Command Issued: " + username + ", Address:" + socket.getLocalAddress() + ", Port: "
                            + socket.getPort() + ", Time: " + logonTime + ", Command: " + clientSentence);
                    String result = evaluate(clientSentence) + '\n';
                    this.output.writeBytes(result);
                }
            } catch (IOException e) {
                System.out.println("Error: " + e);
                break;
            }
        }
        return; // close the thread
    }

    private int getPrecedence(char operator)
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

    private List<String> infixToPostfix(String expression)
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
            // token is either an operator or unkown
            else {
                if (!Character.toString(c).matches("[+-//*^]")) {
                    return null;
                }
                postfixExpression.add(nextNum);
                nextNum = "";
                while (!charStack.isEmpty() && getPrecedence(c) <= getPrecedence(charStack.peek())) {
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

    private String evaluate(String expression)
    {
        String resultSentence = "";
        expression = expression.replaceAll("\\s", "");
        List<String> postfixExpression = infixToPostfix(expression);

        if (postfixExpression == null) {
            resultSentence = "ERROR: Invalid expression entered.";
            return resultSentence;
        }

        Stack<Integer> expressionStack = new Stack<>();

        int operand1, operand2;
        for (int i = 0; i < postfixExpression.size(); ++i) {
            String s = postfixExpression.get(i);
            if (s.matches("[+-//*^]")) {
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
                    case "^":
                        result = (int) Math.pow(operand2, operand1);
                        break;
                }
                expressionStack.push(result);
            }
            else {
                try {
                    expressionStack.push(Integer.parseInt(s));
                }
                catch (NumberFormatException e) {
                    return "ERROR: Unknown symbol " + s;
                }
            }
        }

        resultSentence = Integer.toString(expressionStack.pop());
        return resultSentence;
    }

    private void logInfo(String data)
    {
        try {
            File log = new File("logfile.txt");
            if (log.createNewFile())
                System.out.println("File created: " + log.getName());
            FileWriter myWriter = new FileWriter("logfile.txt", true);
            myWriter.write(data + "\n");
            myWriter.close();
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
