package duke;


import java.time.LocalDateTime;

import duke.common.Command;
import duke.common.Response;
import duke.exception.EmptyDescription;
import duke.exception.InvalidTaskNumber;
import duke.exception.InvalidTypeOfTask;
import duke.parser.Parser;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.Todo;
import duke.ui.Ui;
import duke.storage.Storage;


/**
 * Duke program maintains a taskList for user to track tasks.
 * Reads user input tasks(todo, event, deadline).
 * Able to perform add, delete, markasDone tasks.
 */

public class Duke {
    private Storage storage;
    private TaskList taskList;

    /**
     * Initialise Duke chatbot.
     */
    public Duke() {
        storage = new Storage();
        taskList = storage.load();
    }

    public void save() {
        storage.save(taskList.getTasks());
    }

    /**
     * Adds task to tasklist.
     *
     * @param p
     * @throws EmptyDescription
     */
    public String addTask(Parser p) throws EmptyDescription, InvalidTypeOfTask {
        String reply = "";
        Command command = p.getCommand();
        String description = p.getDescription();
        if (description.equals("")) {
            throw new EmptyDescription(p.getTypeOfTask());
        } else {
            LocalDateTime time = p.getTime();
            Task newTask;
            switch (command) {
            case TODO:
                newTask = new Todo(description);
                break;
            case DEADLINE:
                newTask = new Deadline(description, time);
                break;
            case EVENT:
                newTask = new Event(description, time);
                break;
            default:
                throw new InvalidTypeOfTask();
            }
            if (taskList.detectDuplicates(newTask)) {
                reply = "Input already exists. Please try again";
            } else if (!taskList.detectDuplicates(newTask)) {
                taskList.add(newTask);
                String instructions = Response.ADD.toString() + newTask + "\n" + this.status();
                reply = instructions;
            } else {
                reply = "Input already exists. Please try again";
            }
        }
        return reply;
    }
    /**
     * Marks task as DONE.
     *
     * @param p
     * @throws EmptyDescription
     */
    public String markAsDone(Parser p) throws InvalidTaskNumber {
        String reply = "";
        try {
            if (p.getDescription().equals("")) {
                throw new EmptyDescription(p.getTypeOfTask());
            }
            int i = Integer.parseInt(p.getDescription()) - 1;
            reply = taskList.markAsDone(i);
        } catch (EmptyDescription e) {
            System.out.println(e.toString());
        }
        return reply;
    }

    /**
     * Removes task from taskList.
     *
     * @param p
     */
    public String deleteTask(Parser p) throws InvalidTaskNumber {
        int i = Integer.parseInt(p.getDescription()) - 1;
        return taskList.delete(i);
    }

    /**
     * Prints list.
     */
    public String list() {
        return taskList.list();
    }
    /**
     * Locates tasks matched with keyword.
     * @param parser
     */
    public String find(Parser parser) {
        String keyword = parser.getDescription();
        return taskList.find(keyword);
    }


    public String status() {
        return "Now you have " + taskList.getNumberOfTasks() + " tasks in the list.\n";
    }

//    /**
//     * Initialise scanner.
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//        Duke duke = new Duke();
//        System.out.println(duke.getResponse("todo"));
//
//    }

    /**
     * Generate a response to user input.
     */
    public String getResponse(String input) {

        Ui ui = new Ui(this);
        String response = ui.readCommand(input);
        return "Duke response: " + response;
    }


    /**
     * Greets user.
     */
    public String greet() {
        return Response.GREET.toString();
    }

    /**
     * Say bye bye
     */
    public String exit() {
        return Response.EXIT.toString();
    }
}
