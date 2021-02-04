package duke;

import java.io.IOException;
import java.util.Scanner;

import duke.exception.EmptyDescription;
import duke.exception.InvalidTypeOfTask;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Duke program maintains a taskList for user to track tasks.
 * Reads user input tasks(todo, event, deadline).
 * Able to perform add, delete, markasDone tasks.
 */

public class Duke {
    private Storage storage;
    private Ui ui;
    private TaskList taskList;

    /**
     * Initialise Duke chatbot.
     */
    public Duke() {
        ui = new Ui();
        storage = new Storage();
        try {
            taskList = storage.load();
        } catch (IOException e) {
            System.out.println("file not found");
        }
    }

    /**
     * Start Duke chat services.
     */
    public void execute() {
        ui.greet();
        Boolean shouldExit = false;
        Scanner s = new Scanner(System.in);

        while (!shouldExit && s.hasNextLine()) {
            try {
                taskList = storage.load();
                taskList = ui.readCommand(taskList, s);
                shouldExit = ui.getExit();
                storage.save(taskList.getTasks());
            } catch (EmptyDescription e) {
                ui.enclose(e.toString());
            } catch (InvalidTypeOfTask e) {
                ui.enclose(e.toString());
            } catch (IOException e) {
                System.out.println("exception");
            }
        }
        ui.exit();
    }

    /**
     * Initialise scanner.
     *
     * @param args
     */
    public static void main(String[] args) {
        Duke duke = new Duke();
        duke.execute();
    }

    /**
     * You should have your own function to generate a response to user input.
     * Replace this stub with your completed method.
     */
    public String getResponse(String input) {
        return "Duke heard: " + input;
    }
}
