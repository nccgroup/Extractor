/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burp;


import java.awt.Component;
import java.util.Iterator;
import java.util.LinkedList;
       
/**
 *
 * @author nbidron
 */
public abstract class queryNode{
    
    public String name;
    public String[] setting;
    public String[] settingName;
    public int settingsNumber;
    //add a node type and store the node's type, the string returned on toString should get the GUI a node name based on type and settings
    
    public byte[] previousBlob;
    public byte[] currentBlob;
    
    protected Boolean isLeafNode;
    protected nodeMessageTabFactory factory;
    public queryNode parentNode;
    
    
    
    public queryNode(String arg, queryNode parent) {
        //check here the string param to make a node of the correct type
        name = arg;
        parentNode = parent;
        setting = new String[10];
        for (int i = 0; i < setting.length; ++i) {
            setting[i] = "";
        }
        settingsNumber = 0;
        settingName = new String[10];
        
        for (int i = 0; i < settingName.length; ++i) {
            settingName[i] = "Unused parameter:";
        }
        
        isLeafNode = false;
        factory = new nodeMessageTabFactory(this);
        
        if(parent != null) {
            setAsLeafNode();
        }
    }
    
    public void close() {
        setAsNonLeafNode();
    }
    
    public String getSetting() {
        return setting[0];
    }
    
    public String getSetting(int i) {
        return setting[i];
    }
    
    public String getSettingName(int i) {
        return settingName[i];
    }
    
    public void setSetting(String arg) {
        setting[0] = arg;
        //refreshTabNAme();
    }
    
    public void refreshTabNAme() {
        //to show accurate names we cycle leaf node status as it will 
        //deregister the editor tab and reregister it)
        if(isLeafNode) {
            setAsNonLeafNode();
            setAsLeafNode();
        }
    }
    
    public void setSetting(String arg, int i) {
        setting[i] = arg;
    }
    
    public int getSettingsNumber(){
        return settingsNumber;
    }
    
    public String toString() {
        if((settingsNumber > 0) && !(setting[0].isEmpty())) {
            return name + ":" + setting[0];
        } else {
            return name;
        }
    }
    
    public final String shortName(){
        if(this.parentNode != null){
            return parentNode.shortName()+ ">";
        }else {
            return ">";
        }
    }
    
    public final String fullName() {
        if((this.parentNode != null) && (Globals.longNames == true)){
            return parentNode.fullName() + ">" + toString();
        }else if((this.parentNode != null) && (Globals.longNames == false)){
            return parentNode.shortName() + toString();
        }else {
            return toString();
        }
    }

    public abstract byte[] unpackData(byte[] data);
    public abstract byte[] repackData(byte[] context, byte[] data);
    
    public void setAsLeafNode() {
        if(!isLeafNode){
            Globals.callbacks.registerMessageEditorTabFactory(factory);
            isLeafNode = true;           
        }
    }
    
    public void setAsNonLeafNode() {
        if(isLeafNode) {
            isLeafNode = false;
            Globals.callbacks.removeMessageEditorTabFactory(factory);
        }
    }    
    
    class nodeMessageTabFactory implements IMessageEditorTabFactory {
        @Override
        public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable)
        {
            // create a new instance of our custom editor tab
            return new NodeDecodedTab(controller, editable, node);
        }
        
        public nodeMessageTabFactory(queryNode ownerNode) {
            this.node = ownerNode;
        }
        
        private queryNode node;
    }
    
    
    class NodeDecodedTab implements IMessageEditorTab
    {
        private boolean editable;
        private ITextEditor txtInput;
        private byte[] currentMessage;
        private boolean isRead;
        private boolean isValid; 
        private queryNode node;
        private LinkedList<byte[]> contentList;
                

        public NodeDecodedTab(IMessageEditorController controller, boolean editable)
        {
            this.editable = editable;
            this.isRead = false;
            this.isValid = false;

            // create an instance of Burp's text editor, to display our deserialized data
            txtInput = Globals.callbacks.createTextEditor();
            txtInput.setEditable(editable);
        }
        
        public NodeDecodedTab(IMessageEditorController controller, boolean editable, queryNode ownerNode)
        {
            this.editable = editable;
            this.isRead = false;
            this.isValid = false;
            this.node = ownerNode;
            contentList = new LinkedList<byte[]>();

            // create an instance of Burp's text editor, to display our deserialized data
            txtInput = Globals.callbacks.createTextEditor();
            txtInput.setEditable(editable); 
        }

        @Override
        public String getTabCaption() //called by burp, sets tab title
        {
            return node.fullName();
        }

        @Override
        public Component getUiComponent()
        {
            return txtInput.getComponent();
        }

        @Override
        public boolean isEnabled(byte[] content, boolean isRequest)
        {
            //enable this tab all the time:
            //since we need to look at the data in depth to know if there is 
            //valid content, we always show the tab
            
            return true;
        }

        @Override
        public void setMessage(byte[] content, boolean isRequest)
        {
            if ((content == null) || (content.length < 1))
            {
                txtInput.setText("".getBytes());
                txtInput.setEditable(false);
            }
            else
            {
                LinkedList <queryNode>nodeList;
                nodeList = new LinkedList<>();
                nodeList.add(node);
                queryNode tempNode = node;

                while((tempNode != null) && (tempNode.parentNode != null)) {
                    nodeList.addFirst(tempNode.parentNode);
                    tempNode = tempNode.parentNode;
                }                    

                
                byte[] data = content;
                
                Iterator<queryNode> itr=nodeList.iterator();  
                while(itr.hasNext()){  
                    if(data.length > 0) {
                        queryNode tmpNode = itr.next();
                        contentList.add(data);
                        data = tmpNode.unpackData(data);
                    }else {
                        queryNode tmpNode2 = itr.next();
                        data = "no Data".getBytes();
                    }
                }                
                
                if(data.length > 0) {
                    txtInput.setText(data);
                    txtInput.setEditable(isRequest);
                    this.isValid = true;
                }else {
                    txtInput.setText("no data".getBytes());
                    txtInput.setEditable(false);
                    this.isValid = false;
                }                
            }
            
            // remember the query content we'll need it to modify later
            currentMessage = content;
        }

        @Override
        public byte[] getMessage()
        {
            if (txtInput.isTextModified())
            {
                byte[] text = txtInput.getText();
                String modifiedData = "noData";
                
                
                
                LinkedList <queryNode>nodeList;
                nodeList = new LinkedList<>();
                nodeList.add(node);
                queryNode tempNode = node;

                while((tempNode.parentNode != null) && (tempNode != null)) {
                    nodeList.addFirst(tempNode.parentNode);
                    tempNode = tempNode.parentNode;
                }                    


                byte[] data = text;
                Iterator<queryNode> itr=nodeList.descendingIterator();
                Iterator<byte[]> itrDat=contentList.descendingIterator();
                
                while(itr.hasNext()){  
                    queryNode tmpNode = itr.next();
                    byte[] tmpData = itrDat.next();
                    data = tmpNode.repackData(tmpData, data);
                }
                                
                return data;
            }
            else {
                return currentMessage;
            }
        }

        @Override
        public boolean isModified()
        {
            return txtInput.isTextModified();
        }

        @Override
        public byte[] getSelectedData()
        {
            return txtInput.getSelectedText();
        }
    }
}
