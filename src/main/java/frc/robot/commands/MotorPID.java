// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SuperDrive;

public class MotorPID extends CommandBase {
  /** Creates a new RunMotor. */

  private SuperDrive sd;
  private double setPoint;
  public double kP, kI, kD;

  public MotorPID(SuperDrive sd) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.sd = sd;
    setPoint = 0;
    addRequirements(sd);
  }

  public void setSetPoint(double setPoint){
    this.setPoint = setPoint;
  }

  public double getSetPoint(){
    return setPoint;
  }

  public void setPID(double kP, double kI, double kD){
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    sd.setPID(kP, kI, kD);
    sd.setSetPoint(setPoint);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}