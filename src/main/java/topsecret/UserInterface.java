package topsecret;

import java.io.IOException;

public class UserInterface {
        /**
        * Command Line Utility
        */
        public static void main(String[] args) throws IOException {
            //Check if it's a help command or arg command first
            if (args.length > 0) {
                String firstArg = args[0];
                //check help menu first
                if (firstArg.equals("-h") || firstArg.equals("--help")) {
                    printHelpMenu();
                    return;
                //check for debug args
                } else if (firstArg.equals("-a") || firstArg.equals("--args")) {
                    displayArgs(args);
                    return;
                }
            }
            // Validate correct number of arguments (0, 1, or 2)
            if(args.length  > 2) {
                System.out.println("Too many arguments provided. Use -h or --help for usage information.");
                return;
            }
            //validate that the first argument is a number if it exists
            if(args.length > 0) {
                try {
                    int fileIndex = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.out.println("First argument must be a number representing the file index. Use -h or --help for usage information.");
                    return;
                }
            }
            //Inputs are valid at this point so pass them on
            ProgramControl programControl = new ProgramControl(new FileHandler());
            if(args.length == 0) {
                System.out.println("Listing files:");
                //call program control with no args
                System.out.println(programControl.handleArgs());
            } else if (args.length == 1) {
                System.out.println("Displaying file contents:");
                //call program control with file index arg
                System.out.println(programControl.handleArgs(Integer.parseInt(args[0])));

            } else if (args.length == 2) {
                System.out.println("Displaying file contents with custom cipher:");
                //call program control with file index and cipher args
                System.out.println(programControl.handleArgs(Integer.parseInt(args[0]), args[1]));

            }


        }

        private static void displayArgs(String[] args){
            System.out.println("Arguments Received:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("Argument " + i + ": " + args[i]);
            }
        }

        private static void printHelpMenu(){
            System.out.println("Help Menu:");
            System.out.println("Usage: java TopSecret [option] [arguments]");
            System.out.println("Options:");
            System.out.println("  none                  List all files in the current directory");
            System.out.println("  <file_int>            Dechipher and display the contents of the specified file (by index)");
            System.out.println("  <file_int> <string)   Dechipher the specified file (by index) using the provided string as the key");
            System.out.println("  -h, --help            Display this help menu");
            System.out.println("  -a, --args            Display the arguments passed to the program");
        }

}
