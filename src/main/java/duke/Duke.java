package duke;

import duke.task.Task;
import duke.task.ToDo;
import duke.task.Event;
import duke.task.Deadline;
import duke.task.TaskList;
import duke.command.Commands;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Class encapsulating a duke.Duke and its commands.
 */
class Duke {

    private enum UserCommands {
        DONE, TODO, DEADLINE, EVENT, GET, DELETE, LIST, BYE;
    }

    private class Parser {
        private String[] list_of_words = new String[0];
        private String userInput ="";
        private DateTimeManager manager = new DateTimeManager(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        private int taskIndex = -1;
        private LocalDate date;

        private UserCommands parseString(String userInput) throws DukeException {
            this.userInput = userInput;

            if (userInput.equals("bye")) {
                return UserCommands.BYE;
            } else if (userInput.equals("list")) {
                return UserCommands.LIST;
            }

            // Separate them with space
            String[] arrOfCommandWords = userInput.split(" ");
            if (arrOfCommandWords.length <= 1) {
                handleInvalidInputs(userInput);
            }

            this.list_of_words = arrOfCommandWords;
            // Check the command word
            String commandWord = arrOfCommandWords[0];
            switch (commandWord) {
            case "todo":
                return UserCommands.TODO;
            case "deadline":
                parseDescription(userInput, "/by ");
                return UserCommands.DEADLINE;
            case "event":
                parseDescription(userInput, "/at ");
                return UserCommands.EVENT;
            case "done":
                try {
                    int index = Integer.parseInt(arrOfCommandWords[1]) - 1;
                    this.taskIndex = index;
                } catch (NumberFormatException e) {
                    throw new DukeException("Invalid duke.task number");
                }
                return UserCommands.DONE;
            case "get":
                try {
                    manager.parseDateTime(arrOfCommandWords[1]);
                    return UserCommands.GET;
                } catch (DateTimeParseException e) {
                    throw new DukeException("Invalid date format.");
                }
            case "delete":
                try {
                    int index = Integer.parseInt(arrOfCommandWords[1]) - 1;
                    this.taskIndex = index;
                } catch (NumberFormatException e) {
                    throw new DukeException("Invalid duke.task number");
                }
                return UserCommands.DELETE;
            default:
                throw new DukeException("Sorry, I don't know what that means.");
            }
        }

        private void parseDescription(String userInput, String command) {
            try {
                int indexOfDate = userInput.indexOf(command);
                int startOfDescription = userInput.indexOf(' ');
                if (indexOfDate < 0) {
                    throw new DukeException("No date specified for duke.task.");
                }
                String description = userInput.substring(startOfDescription, indexOfDate);
                list_of_words[1] = description;
                LocalDate date = manager.parseDateTime(userInput.substring(indexOfDate + command.length()));
                this.date = date;
            } catch (DateTimeParseException | DukeException e) {
                System.out.println(e.getMessage());
            }
        }

        /**
         * Method for duke.Duke to handle invalid inputs by the user.
         *
         * @param input The user input to duke.Duke.
         */
        private void handleInvalidInputs(String input) {
            try {
                switch (input) {
                case "todo": // fallthrough
                case "deadline": // fallthrough
                case "event": {
                    throw new DukeException(
                            String.format(
                                    "☹ OOPS!!! The description of a %s cannot be empty.",
                                    input
                            )
                    );
                }
                case "done": // fallthrough
                case "delete":
                    throw new DukeException("Please enter the duke.task index.");
                case "get":
                    throw new DukeException("Please enter a date in dd/MM/yyyy format.");
                default:
                    throw new DukeException(
                            "☹ OOPS!!! I'm sorry, but I don't know what that means :-("
                    );
                }
            } catch (DukeException e) {
                System.out.println(String.format("%4s%s",
                        " ", e.getMessage()));
            }
        }


        private void executeTasks(UserCommands type) {
            switch (type) {
            case BYE:
                exit();
                break;
            case LIST:
                returnTaskList();
                break;
            case GET:
                returnTasksOnDate(list_of_words[1]);
                break;
            case TODO:
                updateTasks(new ToDo(list_of_words[1]));
                break;
            case DEADLINE:
                updateTasks(new Deadline(list_of_words[1], this.date));
                break;
            case EVENT:
                updateTasks(new Event(list_of_words[1], this.date));
                break;
            case DELETE:
                deleteTask(this.taskIndex);
                break;
            case DONE:
                markTaskAsCompleted(this.taskIndex);
                break;
            }
        }

    }

    /**
     * Field for duke to keep track of duke.task list.
     */
    private TaskList taskList;
    private Storage storage;
    private HashMap<LocalDate, ArrayList<Task>> dateTasks = new HashMap<>();
    private Ui ui;
    private Parser parser = new Parser();

    /**
     * Constructor for duke.Duke
     */
    public Duke(TaskList taskList, Storage storage, Ui ui) {
        this.taskList = taskList;
        this.storage = storage;
        this.ui = ui;
    }

    /**
     * Dividing line for formatting duke.Duke's replies.
     */
    private void divider() {
        StringBuilder builder = new StringBuilder(100);
        Stream.generate(() -> '-').limit(60).forEach(e -> builder.append(e));

        String line = String.format("%4s+%s+\n", " ", builder.toString());
        System.out.println(line);
    }

    /**
     * Method that prints duke.Duke's greetings.
     */
    private void greet() {
        divider();
        ui.outputMessage(Commands.GREET);
        divider();
    }

    /**
     * Method that prints duke.Duke's exit message.
     */
    private void exit() {
        divider();
        ui.outputMessage(Commands.EXIT);
        divider();
    }

    /**
     * Method that prints the current tasks in the duke.task list.
     */
    private void returnTaskList() {
        divider();
        ui.outputMessage(Commands.LIST);
        System.out.println(taskList);
        divider();
    }

    /**
     * Returns the tasks on a given date.
     *
     * @param dateTime The desired date.
     */
    private void returnTasksOnDate(String dateTime) {
        String customPattern ="dd/MM/yyyy";
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(customPattern);
        DateTimeManager dateTimeManager = new DateTimeManager(customFormatter);
        LocalDate date = dateTimeManager.parseDateTime(dateTime);
        System.out.println(dateTasks.getOrDefault(date, new ArrayList<>()));
    }


    private void updateTasks(Task task) {
        this.taskList = this.taskList.add(task);
        divider();
        ui.outputMessage(Commands.ADD);
        System.out.println(
                String.format("%5s%s\n%4s%s", " ", task,
                        " ", this.taskList.status())
        );
        divider();
        storage.addTaskToFile(task);
    }


    /**
     * Method for duke.Duke to mark the respective tasks as completed.
     *
     * @param index Index of the duke.task to be deleted.
     */
    private void markTaskAsCompleted(int index) {
        try {
            boolean isValid = this.taskList.isValidTaskIndex(index);
            divider();
            if (isValid) {
                String toUpdate = this.taskList.getTask(index).toString();
                Task task = this.taskList.markTaskAsCompleted(index);
                ui.outputMessage(Commands.DONE);
                System.out.println(
                        String.format("%6s%s\n%4s%s", " ", task,
                                " ", this.taskList.status())
                );
                storage.markTaskAsCompleted(task.toString(), toUpdate);
            } else {
                throw new DukeException("There is no such duke.task.");
            }
            divider();
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method for duke.Duke to delete the corresponding duke.task.
     *
     * @param index Index of the duke.task to be deleted.
     */
    private void deleteTask(int index) {
        try {
            boolean isValid = taskList.isValidTaskIndex(index);
            divider();
            if (isValid) {
                Task task = taskList.getTask(index);
                this.taskList = this.taskList.deleteTask(index);
                ui.outputMessage(Commands.DELETE);
                System.out.println(
                        String.format("%6s%s\n%4s%s", " ", task,
                                " ", taskList.status())
                );
                storage.deleteTaskFromFile(this.taskList);
            } else {
                throw new DukeException("There is no such duke.task.");
            }
            divider();
        } catch (DukeException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Runs the duke.Duke chatbot.
     */
    private void run() {

        this.taskList = storage.loadData(this.dateTasks, this.taskList);

        // Greeting the user
        greet();

        // Taking in commands
        Scanner sc = new Scanner(System.in);

        while (true) {
            String command = sc.nextLine().strip();
            try {
                UserCommands type = parser.parseString(command);
                parser.executeTasks(type);
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            } finally {

            }
        }
        // sc.close();
    }

    /**
     * Main method to execute duke.Duke's functions.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        String directoryPath = "./data";
        String filePath = "./data/duke.text";
        File directory = new File(directoryPath);
        // Check folder exists
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Successfully created directory.");
            } else {
                System.out.println("An error occurred");
            }
        }

        Duke duke = new Duke(new TaskList(), new Storage(filePath), new Ui());
        duke.run();
    }
}
