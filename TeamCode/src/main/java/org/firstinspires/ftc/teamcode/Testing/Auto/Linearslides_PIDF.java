package org.firstinspires.ftc.teamcode.Testing.Auto;

import static org.firstinspires.ftc.teamcode.drive.Variables.Autonomous_Variables.motorRiseyRise;
import static org.firstinspires.ftc.teamcode.drive.Variables.Autonomous_Variables.motorLiftyLift;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Config
@Disabled
@Autonomous(group = "drive")
public class Linearslides_PIDF extends OpMode {


    private PIDController controller;
    private PIDController controller2;
    // P = How quickly the thing moves, D = Dampener of how much it slows down at the end (Only 2 you should adjust)
    // the letter after the main
    public static double Pl = 0.021, Il = 0, Dl = 0.0004;

    public static double Pr = 0.021, Ir = 0, Dr = 0.0004;

    // Feedforward Component of the linear slides
    public static double f = 0;

    public static int target1 = 700;

    public final double ticks_in_degrees = 1993.6 / 180;

        @Override
        public void init() {
            controller = new PIDController(Pr, Ir,Dr);
            controller2 = new PIDController(Pl, Il,Dl);
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

            motorLiftyLift = hardwareMap.get(DcMotor.class,"motorLiftyLift");
            motorRiseyRise = hardwareMap.get(DcMotor.class,"motorRiseyRise");
}

        @Override
        public void loop() {
            controller.setPID(Pr, Ir,Dr);
            controller2.setPID(Pl, Il,Dl);
            int LinearSlide_Pos1 = motorRiseyRise.getCurrentPosition();
            int LinearSlide_Pos2 = motorLiftyLift.getCurrentPosition();

            double pidR = controller.calculate(LinearSlide_Pos1,target1);
            double pidL = controller2.calculate(LinearSlide_Pos2, target1);
            double ff = Math.cos(Math.toRadians(target1 / ticks_in_degrees)) * f;

            double powerR = pidR + ff;
            double powerL = pidL + ff;

            motorRiseyRise.setPower(powerR);
            motorLiftyLift.setPower(powerL);

            telemetry.addData("Risey Rise Pos", LinearSlide_Pos1);
            telemetry.addData("LiftyLift Pos", LinearSlide_Pos2);
            telemetry.addData("Target Pos", target1);
            telemetry.update();}




    }

