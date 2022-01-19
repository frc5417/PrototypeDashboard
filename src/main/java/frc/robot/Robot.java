// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Map;
import java.util.function.DoubleSupplier;

import edu.wpi.first.networktables.NetworkTableEntry;
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

  private int nMotors = 0;
  private NetworkTableEntry nMotorsEntry;
  private int ports[];
  private RunMotor runs[];
  private MotorPID pids[];
  private SuperDrive sd[];
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
    tab.add("Instructions", "To use CANSPARKMAX motors, use ports 1-10. To use TALON motors, use ports 11-20. To use VICTOR motors, use ports 21-30.")
    .withWidget(BuiltInWidgets.kTextView)
    .withSize(1, 6)
    .getEntry();
    nMotorsEntry = tab.add("Number of Motors: ", 0)
    .withWidget(BuiltInWidgets.kNumberSlider)
    .withProperties(Map.of("min",0,"max",32,"step",1))
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
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    nMotorsEntry.getDouble(0);
    if(nMotors == 0){
      nMotors = updSet("Number of Motors: ");
      //System.out.println(nMotors);
      if(nMotors > 0){
        SmartDashboard.delete("Number of Motors: ");
        ports = new int[nMotors];
        runs = new RunMotor[nMotors];
        pids = new MotorPID[nMotors];
        sd = new SuperDrive[nMotors];
      }
    }else{
      for(int i = 0; i < nMotors; i++){
        if(ports[i] > 0){
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
        }else{
          ports[i] = updSet("Port for Motor #" + i + ":");
          if(ports[i] > 0){
            sd[i] = new SuperDrive(ports[i]);
            runs[i] = new RunMotor(sd[i]);
            pids[i] = new MotorPID(sd[i]);
            SmartDashboard.delete("Port for Motor #" + i + ":");
          }
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
