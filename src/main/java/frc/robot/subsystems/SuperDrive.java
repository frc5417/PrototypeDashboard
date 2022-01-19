// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SuperDrive extends SubsystemBase {

  private CANSparkMax can;
  private WPI_TalonSRX talon;
  private WPI_VictorSPX victor;
  private int type = 0;

  public SuperDrive(int port) {
    if(port <= Constants.CANLIM){
      can = new CANSparkMax(port, MotorType.kBrushless);
      type = 1;
    }else if(port <= Constants.TALLIM){
      talon = new WPI_TalonSRX(port);
      type = 2;
    }else if(port <= Constants.VICLIM){
      victor = new WPI_VictorSPX(port);
      type = 3;
    }
  }

  public void setPower(double speed){
    if(type == 1){
      can.set(speed);
    }
    if(type == 2){
      talon.set(speed);
    }
    if(type == 3){
      victor.set(speed);
    }
  }

  public void setSetPoint(double setPoint){
    if(type == 1){
      can.getPIDController().setReference(setPoint, ControlType.kVelocity);
    }
  }

  public void setPID(double kP, double kI, double kD){
    if(type == 1){
      can.getPIDController().setP(kP);
      can.getPIDController().setI(kI);
      can.getPIDController().setD(kD);
    }
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
