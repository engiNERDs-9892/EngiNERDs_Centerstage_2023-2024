package org.firstinspires.ftc.teamcode.autos;


import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.AirplaneMountServo;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.DegreeArm;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.DegreeWrist;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.FlippyFlip;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.FlooppyFloop;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.GearServo;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.LeftClaw;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.Open;
import static org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables.RightClaw;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Examples.BluePipline;
import org.firstinspires.ftc.teamcode.drive.Variables.EngiNERDs_Variables;
import org.firstinspires.ftc.teamcode.drive.opmode.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;


// AUTO PLAN - This is as of Dec 4, 2023
//
// a = Right, b = Middle, c = Left
//
// 1. Drive to the location of the Team Prop and move the team prop out of the way
// 1a. Drive Diagonal Left and Forwards to move the team prop into the middle of the trusses
// 1b. Drive Forwards far enough to move the team prop into the Blue Alliance area
// 1c.  Drive right and forwards to move the team prop into the Blue Alliance area

// 2. Place the purple pixel depending on wherever the Team Prop is located
// 2a. Drive backwards to place the purple pixel on the spikemark
// 2b. Drive backwards to place the purple pixel on the spikemark
// 2c. Drive backwards at a slighty diagnoal to place the purple pixel on the spike mark

// 3. Place the Orange Pixel wherever the Team prop is located
// 3a. Rotate 90 degrees while moving to the far right side of the board to place the orange pixel during auto
// 3b. Rotate 90 degrees while moving to the middle of the backboard to place the orange pixel during auto
// 3c. Rotate 180 degrees while moving to the left side of the backboard to place the orange pixel during auto

// 4. Drive to pick up 2 white pixels (Ideally)


// @ CONFIG is used for FTC Dashboard
@Config
@Disabled
@Autonomous(group = "drive")
public class Blue_Auto extends LinearOpMode {

    // Calls the Variable webcam
    OpenCvWebcam webcam;
    // Calls the proper pipline in order to detect the correct color (in this case its red)
    BluePipline.bluePipline pipeline;
    // This just is determining the default position of the camera detection (this is right due to where our camera is placed)
    BluePipline.bluePipline.Detection_Positions snapshotAnalysis = BluePipline.bluePipline.Detection_Positions.RIGHT; // default

    @Override
    public void runOpMode() {

        // This calls the hardware map for servos and motors
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        new EngiNERDs_Variables(hardwareMap);

        // this call sets the servos during initialization
        FlooppyFloop.setPosition(50 * DegreeArm); // Rotates at an angle forwards
        FlippyFlip.setPosition(45 * DegreeArm); // rotates at an angle forwards
        GearServo.setPosition(225 * DegreeWrist); // Rotates into the air




        // this initializes the camera (Not going into it tooo much but it initalizes the camera + hw map, and the pipline as well)
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        pipeline = new BluePipline.bluePipline();
        webcam.setPipeline(pipeline);

        // This is so we can view what the camera is seeing
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                // This is in what viewing window the camera is seeing through and it doesn't matter
                // what orientation it is | UPRIGHT, SIDEWAYS_LEFT, SIDEWAYS_RIGHT, etc.

                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
            }
        });

        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Realtime analysis", pipeline.getAnalysis());
            telemetry.update();

            // Don't burn CPU cycles busy-looping in this sample
            sleep(50);
        }

        snapshotAnalysis = pipeline.getAnalysis();


        telemetry.addData("Snapshot post-START analysis", snapshotAnalysis);
        telemetry.update();

        // Give the robot a starting position on the coordinate plane
        Pose2d startPoseBlueLeft = new Pose2d(12, 60, Math.toRadians(-90));
        drive.setPoseEstimate(startPoseBlueLeft);


        // This is if the camera detects the left side (code of what it does is below)
        TrajectorySequence blueleftR = drive.trajectorySequenceBuilder(startPoseBlueLeft)

                // Knock the Team Prop out of the way
                .splineToLinearHeading(new Pose2d(9, 40, Math.toRadians(180.00)), Math.toRadians(100))
                .lineToConstantHeading(new Vector2d(8, 32.5))
                .lineToConstantHeading(new Vector2d(0, 32.5))

                // Place the Purple Pixel
                .lineToConstantHeading(new Vector2d(9, 30))
                .waitSeconds(1.5)
                .UNSTABLE_addTemporalMarkerOffset(-1.5, () -> {
                    FlooppyFloop.setPosition(0.03);
                    FlippyFlip.setPosition(0.97);
                })
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    GearServo.setPosition(.98);
                })
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> {
                    RightClaw.setPosition(Open);
                })


                // Raise for the backboard
                .UNSTABLE_addTemporalMarkerOffset(0.5, () -> {
                    FlooppyFloop.setPosition(.15);
                    FlippyFlip.setPosition(.85);
                })
                .UNSTABLE_addTemporalMarkerOffset(1, () -> {
                    GearServo.setPosition(.75);
                })


                // Place the Orange Pixel
                .lineToLinearHeading(new Pose2d(53, 15.75, Math.toRadians(0)))
                .waitSeconds(2)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    LeftClaw.setPosition(Open);
                })

                .lineToLinearHeading(new Pose2d(53, 52, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(48, 10, Math.toRadians(180)))
                .lineToLinearHeading(new Pose2d(62, 10, Math.toRadians(180)))
                .waitSeconds(5)
                .UNSTABLE_addTemporalMarkerOffset(-2, () -> {
                    FlooppyFloop.setPosition(0.03);
                    FlippyFlip.setPosition(0.97);
                })
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    GearServo.setPosition(.98);
                })

                .build();

        // This is if the camera detects the middle (code of what it does is below)
        TrajectorySequence blueleftM = drive.trajectorySequenceBuilder(startPoseBlueLeft)

                // Knock the Team Prop out of the way
                .lineToLinearHeading(new Pose2d(12, 24, Math.toRadians(-90)))

                // Place the purple Pixel
                .lineToLinearHeading(new Pose2d(12, 38, Math.toRadians(-90)))
                .waitSeconds(1)

                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    FlooppyFloop.setPosition(0.03);
                    FlippyFlip.setPosition(0.97);
                })
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> {
                    GearServo.setPosition(.98);
                })
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    RightClaw.setPosition(Open);
                })


                .lineToLinearHeading(new Pose2d(12, 39, Math.toRadians(-90)))
                // Flip the arm to the backboard
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    FlooppyFloop.setPosition(.15);
                    FlippyFlip.setPosition(.85);
                })
                .UNSTABLE_addTemporalMarkerOffset(.5, () -> {
                    GearServo.setPosition(.75);
                })

                // Place the Orange Pixel
                .lineToLinearHeading(new Pose2d(52, 42, Math.toRadians(0)))
                .waitSeconds(1.5)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    LeftClaw.setPosition(Open);
                })
                .lineToLinearHeading(new Pose2d(53, 45, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(48, 10, Math.toRadians(180)))
                .lineToLinearHeading(new Pose2d(62, 10, Math.toRadians(180)))
                .waitSeconds(5)
                .UNSTABLE_addTemporalMarkerOffset(-2, () -> {
                    FlooppyFloop.setPosition(0.03);
                    FlippyFlip.setPosition(0.97);
                })
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    GearServo.setPosition(.98);
                })
                .build();


        // This is if the camera detects the right side (code of what it does is below)
        TrajectorySequence blueleftL = drive.trajectorySequenceBuilder(startPoseBlueLeft)

                // Knock the team prop out of the way
                .lineToLinearHeading(new Pose2d(20, 60, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(20, 10, Math.toRadians(90)))

                // Drop the purple pixel
                .lineToLinearHeading(new Pose2d(20, 40, Math.toRadians(90)))
                .waitSeconds(2)
                .UNSTABLE_addTemporalMarkerOffset(-2, () -> {
                    FlooppyFloop.setPosition(0.03);
                    FlippyFlip.setPosition(0.97);
                })
                .UNSTABLE_addTemporalMarkerOffset(-1.5, () -> {
                    GearServo.setPosition(.98);
                })
                .UNSTABLE_addTemporalMarkerOffset(-0.75, () -> {
                    RightClaw.setPosition(Open);
                })


                // Flip the arm to the backboard
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    FlooppyFloop.setPosition(.15);
                    FlippyFlip.setPosition(.85);
                })
                .UNSTABLE_addTemporalMarkerOffset(.5, () -> {
                    GearServo.setPosition(.75);
                })


                // Play the Orange pixel
                .lineToLinearHeading(new Pose2d(53, 45, Math.toRadians(0)))
                .waitSeconds(1.5)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    LeftClaw.setPosition(Open);
                })

                .lineToLinearHeading(new Pose2d(48, 45, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(48, 10, Math.toRadians(180)))
                .lineToLinearHeading(new Pose2d(62, 10, Math.toRadians(180)))
                .waitSeconds(5)
                .UNSTABLE_addTemporalMarkerOffset(-2, () -> {
                    FlooppyFloop.setPosition(0.03);
                    FlippyFlip.setPosition(0.97);
                })
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> {
                    GearServo.setPosition(.98);
                })

                .build();


        // This is starting after the driver presses play
        waitForStart();


        switch (snapshotAnalysis) {
            case LEFT: // Level 3
            {
                drive.followTrajectorySequence(blueleftL);


                break;

            }


            case RIGHT: // Level 1
            {

                drive.followTrajectorySequence(blueleftR);


                break;
            }

            case CENTER: // Level 2
            {

                drive.followTrajectorySequence(blueleftM);

                break;
            }


        }
    }
}

