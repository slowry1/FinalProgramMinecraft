package finalprogramminecraft;

/*******************************************************
* file: Block.java
* authors: Scott Lowry, Daniel Lynch, Matt Capparelli
* class: CS 445 - Computer Graphics - Fall Section 01
*
* assignment: Final Program
* date last modified: 11/20/2015
*
* purpose: This is the Block object class, which is the component of the Chunk 
* object.  It can be one of six types: Grass, Sand, Dirt, Water, Stone, or 
* Bedrock.  Each block has a status of Active or Inactive, and a vector location
* in 3D space.
*/

//The Block class - component of the Chunk
public class Block {
    
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;
    
    //Define the block types
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        private int BlockID;
        
        BlockType(int i) {
            BlockID=i;
        }
        
        public int GetID(){
        return BlockID;
        }
    
        public void SetID(int i){
            BlockID = i;
        }
    }
    
    //Constructor
    public Block(BlockType type){
        Type= type;
    }
    
    //Set coordinate position
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //Return true if active, false otherwise
    public boolean IsActive() {
        return IsActive;
    }
    
    //Set block status to active
    public void SetActive(boolean active){
        IsActive=active;
    }
    
    //Get ID
    public int GetID(){
        return Type.GetID();
    }
}