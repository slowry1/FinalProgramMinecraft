package finalprogramminecraft;

/*******************************************************
* file: FPCameraController.java
* authors: Scott Lowry, Daniel Lynch, Matt Capparelli
* class: CS 445 - Computer Graphics - Fall Section 01
*
* assignment: Final Program
* date last modified: 11/20/2015
*
* purpose: This file describes the functions necessary for the user to control
* the camera and render what is loaded into view.  The controls are as follows:
*  
*       W/S/A/D And Up/Down/Left/Right (Arrow keys): Forward/Backward/Left/Right
*       Space Bar: Move Up
*       Left Shift: Move Down
*       Mouse: Looks around
*/

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

//This is our First Person Camera Controller class - handles motion, user input
public class FPCameraController {
    //3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lPosition = null;
    
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;
    private Vector3Float me;
    
    //To avoid re-randomizing the block types of the chunk
    private boolean firstTime;
    private Chunk chunkObject;
    

    //Constructor 
    public FPCameraController(float x, float y, float z){
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x,y,z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
        
        firstTime = true;
    }
    
    //increment the camera's current yaw rotation
    public void yaw(float amount){
        //increment the yaw by the amount param
        yaw += amount;
    }
    
    //increment the camera's current pitch rotation
    public void pitch(float amount){
        //increment the pitch by the amount param
        pitch -= amount;
    }
    
    //moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
 }
    
    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
 }
    
    //strafes the camera left relative to its current rotation (yaw)
    public void strafeLeft(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
          }
    
    //strafes the camera right relative to its current rotation (yaw)
    public void strafeRight(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
 }
    
    //moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance){
        position.y -= distance;
    }
    
    //moves the camera down
    public void moveDown(float distance){
        position.y += distance;
    }
    
    //translates and rotate the matrix so that it looks through the camera
    //this does basically what gluLookAt() does
    public void lookThrough(){
        //roatate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
    }
    
    public void gameLoop(){
        FPCameraController camera = new FPCameraController(0, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //length of frame
        float lastTime = 0.0f; // when the last frame was
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = 0.35f;
        //hide the mouse
        Mouse.setGrabbed(true);
        
        // keep looping till the display window is closed the ESC key is down
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
        time = Sys.getTime();
        lastTime = time;
        //distance in mouse movement
        //from the last getDX() call.
        dx = Mouse.getDX();
        //distance in mouse movement
        //from the last getDY() call.
        dy = Mouse.getDY();
        //controll camera yaw from x movement fromt the mouse
        camera.yaw(dx * mouseSensitivity);
        //controll camera pitch from y movement fromt the mouse
        camera.pitch(dy * mouseSensitivity);

        //when passing in the distance to move
        //we times the movementSpeed with dt this is a time scale
        //so if its a slow frame u move more then a fast frame
        //so on a slow computer you move just as fast as on a fast computer
        if (Keyboard.isKeyDown(Keyboard.KEY_W)||Keyboard.isKeyDown(Keyboard.KEY_UP)){//move forward
        camera.walkForward(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)||Keyboard.isKeyDown(Keyboard.KEY_DOWN)){//move backwards
        camera.walkBackwards(movementSpeed);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)||Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {//strafe left
        camera.strafeLeft(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)||Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {//strafe right
        camera.strafeRight(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {//move up
        camera.moveUp(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT )) {
        camera.moveDown(movementSpeed);
        }

        //set the modelview matrix back to the identity
        glLoadIdentity();
        //look through the camera before you draw anything
        camera.lookThrough();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //Draw the scene
        if (firstTime) {
            chunkObject = new Chunk(-30,1,-75);
            firstTime = false;
        }
        chunkObject.render();
        
        //draw the buffer to the screen
        Display.update();
        Display.sync(60);
        }
        Display.destroy();
    }
    

    
//      ***FROM CHECKPOINT #1***   
//
//    This is where the Quads/Lines are created and colored
//    private void render() {
//        try{
//            // Start creating the cube. They are moved back away from the 
//            // camera so that the camera does not appear inside of the cube.
//            glBegin(GL_QUADS);
//            //Top
//            glColor3f(1.0f,0.0f,0.0f);
//            glVertex3f( 1.0f, 1.0f,-3.0f);
//            glVertex3f(-1.0f, 1.0f,-3.0f);
//            glVertex3f(-1.0f, 1.0f, -1.0f);
//            glVertex3f( 1.0f, 1.0f, -1.0f);
//            glEnd();
//            glBegin(GL_QUADS);
//            glColor3f(0.0f,1.0f,0.0f);
//            //Bottom
//            glVertex3f( 1.0f,-1.0f, -1.0f);
//            glVertex3f(-1.0f,-1.0f, -1.0f);
//            glVertex3f(-1.0f,-1.0f,-3.0f);
//            glVertex3f( 1.0f,-1.0f,-3.0f);
//            glEnd();
//            glBegin(GL_QUADS);
//            glColor3f(0.0f,0.0f,1.0f);
//            //Front
//            glVertex3f( 1.0f, 1.0f, -1.0f);
//            glVertex3f(-1.0f, 1.0f, -1.0f);
//            glVertex3f(-1.0f,-1.0f, -1.0f);
//            glVertex3f( 1.0f,-1.0f, -1.0f);
//            glEnd();
//            glBegin(GL_QUADS);
//            glColor3f(1.0f,1.0f,0.0f);
//            //Back
//            glVertex3f( 1.0f,-1.0f,-3.0f);
//            glVertex3f(-1.0f,-1.0f,-3.0f);
//            glVertex3f(-1.0f, 1.0f,-3.0f);
//            glVertex3f( 1.0f, 1.0f,-3.0f);
//            glEnd();
//            glBegin(GL_QUADS);
//            glColor3f(1.0f,0.0f,1.0f);
//            //Left
//            glVertex3f(-1.0f, 1.0f, -1.0f);
//            glVertex3f(-1.0f, 1.0f,-3.0f);
//            glVertex3f(-1.0f,-1.0f,-3.0f);
//            glVertex3f(-1.0f,-1.0f, -1.0f);
//            glEnd();
//            glBegin(GL_QUADS);
//            glColor3f(0.0f,1.0f,1.0f);
//            //Right
//            glVertex3f( 1.0f, 1.0f,-3.0f);
//            glVertex3f( 1.0f, 1.0f, -1.0f);
//            glVertex3f( 1.0f,-1.0f, -1.0f);
//            glVertex3f( 1.0f,-1.0f,-3.0f);
//            glEnd();
//            
//            // Color the lines that make up the square
//            glBegin(GL_LINE_LOOP);
//            //Top
//            glColor3f(0.0f,0.0f,0.0f);
//            glVertex3f( 1.0f, 1.0f,-3.0f);
//            glVertex3f(-1.0f, 1.0f,-3.0f);
//            glVertex3f(-1.0f, 1.0f,-1.0f);
//            glVertex3f( 1.0f, 1.0f,-1.0f);
//            glEnd();
//            glBegin(GL_LINE_LOOP);
//            //Bottom
//            glVertex3f( 1.0f,-1.0f,-1.0f);
//            glVertex3f(-1.0f,-1.0f,-1.0f);
//            glVertex3f(-1.0f,-1.0f,-3.0f);
//            glVertex3f( 1.0f,-1.0f,-3.0f);
//            glEnd();
//            glBegin(GL_LINE_LOOP);
//            //Front
//            glVertex3f( 1.0f, 1.0f,-1.0f);
//            glVertex3f(-1.0f, 1.0f,-1.0f);
//            glVertex3f(-1.0f,-1.0f,-1.0f);
//            glVertex3f( 1.0f,-1.0f,-1.0f);
//            glEnd(); 
//            glBegin(GL_LINE_LOOP);
//            //Back
//            glVertex3f( 1.0f,-1.0f,-3.0f);
//            glVertex3f(-1.0f,-1.0f,-3.0f);
//            glVertex3f(-1.0f, 1.0f,-3.0f);
//            glVertex3f( 1.0f, 1.0f,-3.0f);
//            glEnd();
//            glBegin(GL_LINE_LOOP);
//            //Left
//            glVertex3f(-1.0f, 1.0f,-1.0f);
//            glVertex3f(-1.0f, 1.0f,-3.0f);
//            glVertex3f(-1.0f,-1.0f,-3.0f);
//            glVertex3f(-1.0f,-1.0f,-1.0f);
//            glEnd();
//            glBegin(GL_LINE_LOOP);
//            //Right
//            glVertex3f( 1.0f, 1.0f,-3.0f);
//            glVertex3f( 1.0f, 1.0f,-1.0f);
//            glVertex3f( 1.0f,-1.0f,-1.0f);
//            glVertex3f( 1.0f,-1.0f,-3.0f);
//            glEnd(); 
//        }catch(Exception e){
//        }
//    }    
}

