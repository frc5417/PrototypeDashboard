// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
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

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  ShuffleboardTab tab = Shuffleboard.getTab("Prototype Dashboard");
  NetworkTableInstance inst = NetworkTableInstance.getDefault();

  private NetworkTableEntry nMotorsEntry;
  private int nMotors = 0;
  private NetworkTableEntry toggleBtn;
  private NetworkTableEntry ports[];
  private NetworkTableEntry type[];

  private NetworkTableEntry speeds[];
  private NetworkTableEntry kP[];
  private NetworkTableEntry kI[];
  private NetworkTableEntry kD[];
  private NetworkTableEntry velocity[];
  private NetworkTableEntry setPoint[];

  private int types[];
  private RunMotor runs[];
  private MotorPID pids[];
  private SuperDrive sd[];

  @Override
  public void robotInit() {

    tab.add("How to Use", "To use CANSPARKMAX motors, use type=1. To use TALON motors, use type=2. To use VICTOR motors, use type=3. To use solenoids, use type=4")
    .withWidget(BuiltInWidgets.kTextView)
    .withSize(1, 6)
    .getEntry();

    nMotorsEntry = tab.add("Number of Motors: ", 0).getEntry();
    
    toggleBtn = tab.add("Toggle", false).withWidget(BuiltInWidgets.kToggleButton).getEntry();
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
  
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

  private int updSetInt(NetworkTableEntry entry){
    int ret = entry.getNumber(0).intValue();
    return ret;
  }

  private Boolean updSetBool(NetworkTableEntry entry){
    boolean ret = entry.getBoolean(false);
    return ret;
  }

  private double updSetDouble(NetworkTableEntry entry){
    double ret = (double)entry.getNumber(0.0);
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
    m_robotContainer = new RobotContainer();
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    //toggleBtn.setBoolean(false);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    if(nMotors == 0){ // Select motors!!
      if(updSetBool(toggleBtn)){
        nMotors = updSetInt(nMotorsEntry);
        if(nMotors > 0){ // initialize the motors and arrays and things that store info!!!!
          ports = new NetworkTableEntry[nMotors];
          runs = new RunMotor[nMotors];
          pids = new MotorPID[nMotors];
          sd = new SuperDrive[nMotors];
          type = new NetworkTableEntry[nMotors];
          types = new int[nMotors];
          speeds = new NetworkTableEntry[nMotors];

          kP = new NetworkTableEntry[nMotors];
          kI = new NetworkTableEntry[nMotors];
          kD = new NetworkTableEntry[nMotors];
          velocity = new NetworkTableEntry[nMotors];
          setPoint = new NetworkTableEntry[nMotors];

          for(int i = 0; i < nMotors; i++){
            ShuffleboardLayout layout = tab.getLayout("Motor #"+i);
            ports[i] = layout.add("Port for Motor #" + i + ":", 0).getEntry();
            type[i] = layout.add("Type of Motor #" + i + ":", 0).getEntry();
          }
        }
      }
    }else{
      for(int i = 0; i < nMotors; i++){
        if(types[i] == 0){
          types[i] = updSetInt(type[i]);
          ShuffleboardLayout layout = tab.getLayout("Motor #"+i);

          if(types[i] > 0){ // actually initialize the motors with ports and types
            sd[i] = new SuperDrive(updSetInt(ports[i]), types[i]);
            runs[i] = new RunMotor(sd[i]);
            pids[i] = new MotorPID(sd[i]);
            if(types[i] == 4) {
              speeds[i] = layout.add("Solenoid #" + i + " on or off?", false).withWidget(BuiltInWidgets.kToggleSwitch).getEntry();
            } else {
              speeds[i] = layout.add("Speed for Motor #" + i + ":", 0).getEntry();
            }

            if(types[i] == 1) {
              kP[i] = layout.add("kP for Motor #" + i + ":", 0).getEntry();
              kI[i] = layout.add("kI for Motor #" + i + ":", 0).getEntry();
              kD[i] = layout.add("kD for Motor #" + i + ":", 0).getEntry();
              velocity[i] = layout.add("Velocity for Motor #" + i + ":", 0).getEntry();
              setPoint[i] = layout.add("Set Point for Motor #" + i + ":", 0).getEntry();
            }
          }
        
        }else if(types[i] == 1){ // set speeds and PID for CANSPARKMAXes
          runs[i].setSpeed(updSetDouble(speeds[i]));
          pids[i].setPID(
            updSetInt(kP[i]),
            updSetInt(kI[i]),
            updSetInt(kD[i])
          );
          pids[i].setSetPoint(updSetDouble(setPoint[i]));
          velocity[i].setNumber(sd[i].getVelocity());
          if(Math.abs(runs[i].getSpeed()) > 0.000000001){
            runs[i].schedule();
          }else{
            pids[i].schedule();
          }
        }else{ // set speeds for other motors
          runs[i].setSpeed(updSetInt(speeds[i]));
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
