package seedu.duke.command;

import seedu.duke.Ui;
import seedu.duke.task.TaskList;

/**
 * Represents an exit command. An <code>ExitCommand</code> exits
 * the system when a user types bye to Duke.
 */
public class ExitCommand extends Command {
    private static final String EXIT_MESSAGE = "Bye. Hope to see you again soon!";

    public ExitCommand(Ui ui, TaskList taskList) {
        super(ui, taskList);
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public String getUsageMessage() {
        return "bye | quit the chat bot";
    }

    /**
     * Prints Duke's exit message.
     */
    @Override
    public String execute() {
        return EXIT_MESSAGE;
    }

}
