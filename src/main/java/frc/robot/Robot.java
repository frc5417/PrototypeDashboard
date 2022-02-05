// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.*;
import frc.robot.subsystems.*;
/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  private NetworkTableInstance inst;
  private NetworkTable table;

  private int nMotors = 0;
  private int ports[];
  private RunMotor runs[];
  private MotorPID pids[];
  private SuperDrive sd[];
  private int type[];

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  ShuffleboardTab tab = Shuffleboard.getTab("Prototype Dashboard");

  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
    ShuffleboardTab tab = Shuffleboard.getTab("Instructions");
    tab.add("Instructions", "To use CANSPARKMAX motors, use type=1. To use TALON motors, use type=2. To use VICTOR motors, use type=3. To use solenoids, use type=4")
    .withWidget(BuiltInWidgets.kTextView)
    .withSize(1, 6)
    .getEntry();
    SmartDashboard.getNumber("Number of Motors: ", 0.0);
    SmartDashboard.putString("Click The Box", "The Box");
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    
  }

  private int updSet(String key){
    int ret = (int)SmartDashboard.getNumber(key, 0.0);
    SmartDashboard.putNumber(key, ret);
    return ret;
  }

  private double updSetd(String key){
    double ret = SmartDashboard.getNumber(key, 0.0);
    SmartDashboard.putNumber(key, ret);
    return ret;
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    inst = NetworkTableInstance.getDefault();
    table = inst.getTable("SmartDashboard");
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if(SmartDashboard.getBoolean("reset", false)) {
      for(int i = 0; i < nMotors; i++){
        if(sd[i] != null)
          sd[i].delete();
      }
      nMotors = 0;
      table.getEntry("reset").setBoolean(false);

    }
    if(nMotors == 0){ // Select motors!!
      nMotors = updSet("Number of Motors:");
      //System.out.println(nMotors);
      if(nMotors > 0){ // initialize the motors and arrays and things that store info!!!!
        SmartDashboard.delete("Number of Motors:");
        ports = new int[nMotors];
        runs = new RunMotor[nMotors];
        pids = new MotorPID[nMotors];
        sd = new SuperDrive[nMotors];
        type = new int[nMotors];
      }
    }else{
      for(int i = 0; i < nMotors; i++){
        if(type[i] == 0){
          ports[i] = updSet("Port for Motor #" + i + ":");
          type[i] = updSet("Type of Motor #" + i + ":");
          if(type[i] > 0){ // actually initialize the motors with ports and types
            sd[i] = new SuperDrive(ports[i], type[i]);
            runs[i] = new RunMotor(sd[i]);
            pids[i] = new MotorPID(sd[i]);
            SmartDashboard.delete("Port for Motor #" + i + ":");
            SmartDashboard.delete("Type of Motor #" + i + ":");
          }
        }else if(type[i] == 1){ // set speeds and PID for CANSPARKMAXes
          runs[i].setSpeed(updSetd("Speed for Motor #" + i + ":"));
          pids[i].setPID(
            updSetd("kP for Motor #" + i + ":"),
            updSetd("kI for Motor #" + i + ":"),
            updSetd("kD for Motor #" + i + ":")
          );
          pids[i].setSetPoint(updSetd("Set Point for Motor #" + i + ":"));
          SmartDashboard.putNumber("Velocity for Motor #" + i + ":", sd[i].getVelocity());
          if(Math.abs(runs[i].getSpeed()) > 0.000000001){
            runs[i].schedule();
          }else{
            pids[i].schedule();
          }
        }else if(type[i] == 4){ // do things with solenoid
          runs[i].setSpeed(updSet("Solenoid #" + i + " on or off?"));
          runs[i].schedule();
        }else{ // set speeds for other motors
          runs[i].setSpeed(updSetd("Speed for Motor #" + i + ":"));
          runs[i].schedule();
        }
      }
    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
