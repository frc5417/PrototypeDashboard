// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SuperDrive;

public class RunMotor extends CommandBase {
  /** Creates a new RunMotor. */

  private SuperDrive sd;
  private double speed;

  public RunMotor(SuperDrive sd) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.sd = sd;
    speed = 0;
    addRequirements(sd);
  }

  public void setSpeed(double speed){
    this.speed = speed;
  }

  public double getSpeed(){
    return speed;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    sd.setPower(speed);
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
