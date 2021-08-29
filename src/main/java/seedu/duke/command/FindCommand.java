package seedu.duke.command;

import seedu.duke.DukeException;
import seedu.duke.Storage;
import seedu.duke.Ui;
import seedu.duke.task.Task;
import seedu.duke.task.TaskList;

public class FindCommand extends Command {
    private static final String FIND_MESSAGE = "Here are the matching tasks in your list.\n";
    private String keyword;

    public FindCommand(Ui ui, TaskList taskList, String keyword) {
        super(ui, taskList);
        this.keyword = keyword;
    }

    @Override
    public String getUsageMessage() {
        return "find <keyword> | find tasks by the keyword";
    }

    /**
     * Finds matching tasks from the task list.
     */
    @Override
    public void execute() throws DukeException {
        TaskList matchingTasks = taskList.findTasksByKeyword(keyword);

        if (matchingTasks.isEmpty()) {
            throw new DukeException("No tasks matches the given keyword.");
        }

        ui.divide();
        ui.outputMessage(FIND_MESSAGE);
        ui.outputMessage(matchingTasks.toString());
        ui.divide();
    }

}
