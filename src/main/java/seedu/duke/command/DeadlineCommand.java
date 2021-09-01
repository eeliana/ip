package seedu.duke.command;

import seedu.duke.DateTimeManager;
import seedu.duke.Storage;
import seedu.duke.Ui;
import seedu.duke.task.Deadline;
import seedu.duke.task.Task;
import seedu.duke.task.TaskList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a deadline command. A <code>DeadlineCommand</code> describes
 * the action to be executed when a user input a deadline task description.
 */
public class DeadlineCommand extends Command {
    private static final String ADD_MESSAGE = "Got it. I've added this task:\n";
    private Task task;
    private LocalDate date;
    private Storage storage;

    /**
     * Public constructor for <code>DeadlineCommand</code>.
     * @param ui The Ui to handle user interactions.
     * @param taskList The task list to be updated.
     * @param description The description of the task.
     * @param date Deadline of the task.
     * @param storage The storage to handle modifications to the file.
     */
    public DeadlineCommand(Ui ui, TaskList taskList, String description,
                           LocalDate date, Storage storage) {
        super(ui, taskList);
        task = new Deadline(description, date);
        this.date = date;
        this.storage = storage;
    }

    @Override
    public boolean updatesTaskList() {
        return true;
    }

    @Override
    public String getUsageMessage() {
        return "deadline <description> /by dd/MM/yyy |"
                + " add a deadline task to your list with the given description";
    }

    /**
     * Adds the Deadline task to the task list.
     */
    @Override
    public String execute() {
        taskList = taskList.add(task);
        storage.addTaskToFile(task);

        return String.format("%s\n%s\n%s",
                ADD_MESSAGE, task, taskList.status());
    }

    public TaskList getUpdatedList() {
        return this.taskList;
    }

    public void updateDateTasks(HashMap<LocalDate, ArrayList<Task>> dateTasks,
                                DateTimeManager manager) {
        manager.updateDateTasks(dateTasks, date, task);
    }

}

