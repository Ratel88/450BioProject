/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003  Laurie Heyer
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *   Contact Information:
 *   Laurie Heyer
 *   Dept. of Mathematics
 *   PO Box 6959
 */

package magictool.task;

import java.util.Vector;

import magictool.Cancelable;
import magictool.Executable;

/**
 * Task is an executable task which contains an executable part, a file to be created,
 * a list of files which it needs to run, a name, and a current status. Task implements
 * TwoStringValue as it can be represented in two strings - its name and current status.
 * Task are created to be executed a later time via the task manager.
 */
public class Task implements TwoStringValue{

  /**executable part of the task which can be run when called*/
  protected Executable exe;
  /**current status of the task*/
  protected int status=0;
  /**list of required file to create new file during execution*/
  protected Vector requiredFiles = new Vector();
  /**name of the created file from this task*/
  protected String createdFile="";
  /**array containing string representation of the status levels*/
  protected static String statusLevels[] = {"Ready", "Waiting", "Running", "Completed", "Failed"};
  /**name of the task*/
  protected String name="";
  /**task is ready to be executed*/
  public static final int READY=0;
  /**task manager is executing tasks in order and this task is waiting in line for execution*/
  public static final int WAITING=1;
  /**task is currently being executed*/
  public static final int RUNNING=2;
  /**task is completed successfully*/
  public static final int COMPLETED=3;
  /**task failed to complete*/
  public static final int FAILED=4;

  /**
   * Constructs a task with a blank name and ready status from given executable, file to
   * be created, and required files for execution
   * @param exe executable to be run
   * @param createdFile file to be created by task
   * @param requiredFiles required file for execution of the task
   */
  public Task(Executable exe, String createdFile, Vector requiredFiles) {
    this.exe=exe;
    status=READY;
    this.createdFile=createdFile;
    this.requiredFiles=requiredFiles;
  }

  /**
   * Constructs a task with ready status from given executable, name, file to
   * be created, and required files for execution
   * @param exe executable to be run
   * @param name name of the task
   * @param createdFile file to be created by task
   * @param requiredFiles required file for execution of the task
   */
  public Task(Executable exe,String name, String createdFile, Vector requiredFiles) {
    this.exe=exe;
    this.name=name;
    this.status=READY;
    this.createdFile=createdFile;
    this.requiredFiles=requiredFiles;
  }

  /**
   * Constructs a task from given executable, name, status, file to
   * be created, and required files for execution
   * @param exe executable to be run
   * @param name name of the task
   * @param status current status of task
   * @param createdFile file to be created by task
   * @param requiredFiles required file for execution of the task
   */
  public Task(Executable exe,String name,int status, String createdFile, Vector requiredFiles) {
    this.exe=exe;
    this.name=name;
    this.status=status;
    this.createdFile=createdFile;
    this.requiredFiles=requiredFiles;
  }

  /**
   * sets the current status of the task
   * @param status current status of the task
   */
  public void setStatus(int status){
    this.status=status;
  }

  /**
   * returns the string representation of the current status of the task
   * @return string representation of the current status of the task
   */
  public String getStatusString(){
    return statusLevels[status];
  }

  /**
   * returns the current status of the task
   * @return current status of the task
   */
  public int getStatus(){
    return status;
  }

  /**
   * returns the name of the task. This method is required by the TwoStringValue interface
   * as the first part of the two strings which represent this task.
   * @return name of the task
   */
  public String getString1(){
    return name;
  }

  /**
   * returns the string representation of the current status of the task. This method is required by the TwoStringValue interface
   * as the second part of the two strings which represent this task.
   * @return string representation of the current status of the task
   */
  public String getString2(){
    return statusLevels[status];
  }

  /**
   * sets the executable part of the task
   * @param exe executable to be executed
   */
  public void setExecutable(Executable exe){
    this.exe=exe;
  }

  /**
   * returns the executable part of the task
   * @return executable part of the task
   */
  public Executable getExecutable(){
    return exe;
  }

  /**
   * executes the task - more specifically the executable part of the task and changes the
   * status accordingly
   */
  public void execute(){
    Thread thread = new Thread(){
      public void run(){
        try{
          if(exe!=null){
            exe.execute();
            status=COMPLETED;
          }
          else status=FAILED;

        }
        catch(Exception e){
          status=FAILED;
        }
      }
    };
    thread.start();
  }

  /**
   * returns whether or not the executable has finished executing
   * @return whether or not the executable has finished executing
   */
  public boolean isFinished(){
    if(exe==null) return true;
    return exe.isFinished();
  }

  /**
   * returns whether or not the task requires a specified file
   * @param name file to check if the task requires
   * @return whether or not the task requires a specified file
   */
  public boolean requiresFile(String name){
    for(int i=0; i<requiredFiles.size(); i++){
      if(name.equals((String)requiredFiles.elementAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * returns the file to be created
   * @return file to be created
   */
  public String getCreatedFile(){
    return createdFile;
  }

  /**
   * returns a list of files necessary to run the task
   * @return list of files necessary to run the task
   */
  public Vector getRequiredFiles(){
    return requiredFiles;
  }

  /**
   * sets the path of the file ot be created
   * @param createdFile path of the file ot be created
   */
  public void setCreatedFile(String createdFile){
    this.createdFile=createdFile;
  }

  /**
   * sets the list of file required to run the task
   * @param requiredFiles list of file required to run the task
   */
  public void setRequiredFiles(Vector requiredFiles){
    this.requiredFiles=requiredFiles;
  }

  /**
   * adds a file to the list of file required to run the task
   * @param filename file to add to the list of file required to run the task
   */
  public void addRequiredFile(String filename){
    requiredFiles.addElement(filename);
  }

  /**
   * remove a file to the list of file required to run the task
   * @param filename file to remove from the list of file required to run the task
   */
  public void removeRequiredFile(String filename){
    requiredFiles.removeElement(filename);
  }

  /**
   * cancels the task if it is cancelable operation
   * @return whether or not the task was cancelled
   */
  public boolean cancel(){
    if(exe instanceof Cancelable){
      ((Cancelable)exe).cancel();
      return true;
    }
    return false;
  }

}