package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.qualcomm.robotcore.util.

@Autonomous(name="AutonomousRedTop")
public class AutonomousRedTop extends LinearOpMode {
    DcMotor FrontLeft;
    DcMotor BackLeft;
    DcMotor FrontRight;
    DcMotor BackRight;
    DcMotor CarouselMotor;
    CRServo Intake;

    private ElapsedTime runtime = new ElapsedTime();

    final double DISTANCE_PER_SECOND = 104.25;
    final double DEGREES_PER_SECOND = 350.0; // approximated

    final double ticksInARotation = 537.7;
    // possible: 18.0346888
    final double theoreticalRadius = 10.2;

    @Override
    public void runOpMode(){ 

        FrontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        BackLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        FrontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        BackRight = hardwareMap.get(DcMotor.class, "BackRight");
        Intake = hardwareMap.get(CRServo.class, "Intake");

        CarouselMotor = hardwareMap.get(DcMotor.class, "CarouselMotor");

        waitForStart();
        
        // did not put this encoder reset in the directional methods, in case programmer wants flexiblity with keeping the encoder ticks at the same value. not sure why but there it instanceof
        encoderMotorReset();
        // Intake.setPower(-1);
        
        // sleep(2000);
        
        // Intake.setPower(0);
        
        // Forward(4, 0.2);
        
        // sleep(1000);
        
        // StrafeLeft(20, 0.2);
        
        // Forward(50, 0.2);
        
        // FrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        // BackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        // BackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        // FrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
        
        // FrontLeft.setPower(0.5);
        // FrontRight.setPower(-0.5);
        // BackLeft.setPower(0.5);
        // BackRight.setPower(-0.5);
        
        Forward(30, 0.1);
        
        telemetry.addLine("hi im here");
        telemetry.update();
        
        // PROBLEM: carousel motor not attached/configured? figure out later
        // CarouselMotor.setPower(1);
        // sleep(1000);
        // CarouselMotor.setPower(0);
        // Forward(20, 0.25);
    }

    public double motorArcLength (int theta) {
        double rad = theta * (Math.PI / 180); //converts angle theta in degrees to radians
        return rad * theoreticalRadius; //isolates S, arc length
        /*
        all the turning math is done on the assumption that driving a distance as a line
        is the same as driving that distance around a circumference
        as in, the turning motion does not counteract movement along the circumference
        and if all 4 wheels drive for 10 inches, then if half the wheels drive opposite to start turning,
        they would still drive 10 inches, just along the circumference of their rotation
        this is likely not true, but I cannot find math online and can't really model it either
        to correct much, just do testing
        */
    }

    public int motorTicks (double inches) {
        double diameter = 3.5;

        double circumference = Math.PI * diameter;

        double inchesPerTick = circumference / ticksInARotation;

        return (int) Math.floor(inches / inchesPerTick);
    }

    public double LinearSlideTicks(double inches) {

        double circumference = 5.0; // might be wrong if it is then we're FUCKED !

        double inchesPerTick = circumference / ticksInARotation;//approx 0.00929886553 ticks

        return inches / inchesPerTick;
    }

    public void StrafeLeft (double inches, double Power) {
    
        int ticks = motorTicks(inches);
        
        FrontLeft.setTargetPosition(-ticks);
        FrontRight.setTargetPosition(-ticks);
        BackLeft.setTargetPosition(ticks);
        BackRight.setTargetPosition(ticks);

        runMotorEncoders();

        FrontLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
        
    }

    public void StrafeRight (double inches, double Power) {

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders();

        FrontLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(-Power);
        BackRight.setPower(-Power);

        waitForMotorEncoders();
    }

    public void TurnLeft (double inches, double Power) {
        // both left sides go forward
        // both right sides go backwards
        // this makes the robot turn left and stationary

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders();;

        FrontLeft.setPower(-Power);
        BackLeft.setPower(-Power);
        FrontRight.setPower(-Power);
        BackRight.setPower(-Power);

        waitForMotorEncoders();
    }

    public void TurnRight (double inches, double Power) {
        // both right sides go forward
        // both left sides go backwards

        encoderMotorReset();

        setMotorTargets(motorTicks(inches));

        runMotorEncoders();;

        FrontLeft.setPower(Power);
        BackLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
    }

    public void Forward (double inches, double Power) {
        int ticks = motorTicks(inches);
        
        FrontLeft.setTargetPosition(ticks);
        FrontRight.setTargetPosition(-ticks);
        BackLeft.setTargetPosition(ticks);
        BackRight.setTargetPosition(-ticks);

        runMotorEncoders();

        FrontLeft.setPower(Power);
        FrontRight.setPower(Power);
        BackLeft.setPower(Power);
        BackRight.setPower(Power);

        waitForMotorEncoders();
    }

    public void Stop () {
        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);

        encoderMotorReset();
    }

    public void encoderMotorReset() {
        FrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setMotorTargets (int motorTarget) {
        FrontLeft.setTargetPosition(motorTarget);
        FrontRight.setTargetPosition(motorTarget);
        BackLeft.setTargetPosition(motorTarget);
        BackRight.setTargetPosition(motorTarget);
    }

    public void runMotorEncoders () {
        FrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        BackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void waitForMotorEncoders () {
        while (FrontLeft.isBusy() && FrontRight.isBusy() && BackLeft.isBusy() && BackRight.isBusy()) {
            telemetry.addLine("front left power is " + FrontLeft.getPower());
            telemetry.addLine("front right power is " + FrontRight.getPower());
            telemetry.addLine("back left power is " + BackLeft.getPower());
            telemetry.addLine("back right power is " + BackRight.getPower());
            
            telemetry.update();
        }

        Stop();
    }
}
