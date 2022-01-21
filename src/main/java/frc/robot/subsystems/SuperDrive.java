// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SuperDrive extends SubsystemBase {

  private CANSparkMax can;
  private WPI_TalonSRX talon;
  private WPI_VictorSPX victor;
  private Solenoid solenoid;
  private int type = 0;

  public SuperDrive(int port, int type) {
    this.type = type;
    if(type == 1){
      can = new CANSparkMax(port, MotorType.kBrushless);
      type = 1;
    }else if(type == 2){
      talon = new WPI_TalonSRX(port);
      type = 2;
    }else if(type == 3){
      victor = new WPI_VictorSPX(port);
      type = 3;
    }else if(type == 4){
      solenoid = new Solenoid(PneumaticsModuleType.CTREPCM, port);
    }
  }

  public void setPower(double speed){
    if(type == 1){
      can.set(speed);
    }else if(type == 2){
      talon.set(speed);
    }else if(type == 3){
      victor.set(speed);
    }else if(type == 4){
      solenoid.set(speed > 0.0000001);
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

  public double getVelocity(){
    if(type == 1){
      return can.getEncoder().getVelocity();
    }else{
      return 0.0;
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
