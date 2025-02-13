package controllers;

import exceptions.DukeException;
import models.commands.DeleteCommand;
import models.commands.DoneCommand;
import models.tasks.ITask;
import models.tasks.TaskList;
import views.CLIView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConsoleInputController implements IViewController {

    private CLIView consoleView;
    private TaskFactory taskFactory;
    private TaskList taskList;
    private String filePath = "D:\\For Git\\CS2113_Duke\\src\\main\\java\\saves\\TaskLists.txt";

    /**
     * Constructor.
     * @param view : takes in a View model, in this case a command line view.
     */
    public ConsoleInputController(CLIView view) {
        this.consoleView = view;
        this.taskFactory = new TaskFactory();
        this.taskList = new TaskList();
    }

    /**
     * Method that is called upon receiving commands from CLI.
     * @param input : Input typed by user into CLI
     */
    @Override
    public void onCommandReceived(String input) {
        if (input.equals("bye")) {
            consoleView.end();
        } else if (input.equals("list")) {
            consoleView.printAllTasks(taskList);
        } else if (input.contains("done")) {
            DoneCommand doneCommand = new DoneCommand(input);
            consoleView.markDone(taskList, doneCommand);
            saveData();
        } else if (input.contains("delete")) {
            DeleteCommand deleteCommand = new DeleteCommand(input);
            consoleView.deleteTask(taskList, deleteCommand);
            saveData();
        } else if (input.contains("find")) {
            try {
                consoleView.findTask(taskList, input);
            } catch (ArrayIndexOutOfBoundsException newException) {
                consoleView.invalidCommandMessage(newException);
            }
        } else {
            try {
                ITask newTask = taskFactory.createTask(input);
                taskList.addToList(newTask);
                consoleView.addMessage(newTask, taskList);
                saveData();
            } catch (DukeException newException) {
                consoleView.invalidCommandMessage(newException);
            }
        }
    }
    // TODO refactor saving data and reading data to repository/database

    /**
     * Method that is called in order to saveData to persistent storage.
     */
    public void saveData() {
        try {
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(taskList);

            out.flush();
            out.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that is called when reading data from persistent storage.
     * If file read is empty, will throw EOFException which is suppressed.
     * If file read is corrupted, will throw IOException which is suppressed also.
     */
    public void readData() {
        try {
            FileInputStream file = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(file);

            this.taskList = (TaskList) in.readObject();

            in.close();
            file.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.getSuppressed();
        }
    }
}
