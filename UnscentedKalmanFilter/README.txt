Necessary imports:
EJML-core-0.27.jar
EJML-dense64-0.27.jar

Point needs to be refactored and updated to the new FieldPoint.java and StateModel needs to be added to the server application model.

BallUKF.java and RobotUKF.java are the actual filter classes which can be used in the server application. After using run with the measured position you can get the filtered values for this robot / ball by using the get functions.
