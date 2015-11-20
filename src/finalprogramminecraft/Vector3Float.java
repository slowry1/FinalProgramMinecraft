package finalprogramminecraft;

/*******************************************************
* file: Vector3Float.java
* authors: Scott Lowry, Daniel Lynch, Matt Capparelli
* class: CS 445 - Computer Graphics - Fall Section 01
*
* assignment: Final Program
* date last modified: 11/20/2015
*
* purpose: A vector object class, used to store the camera's position in 3D
* space.
*/

//A 3D vector class
public class Vector3Float {
    
    public float x, y, z;
    
    //Construct the vector
    public Vector3Float(int x, int y, int z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
}
