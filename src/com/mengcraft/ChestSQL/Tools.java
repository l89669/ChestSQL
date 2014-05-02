package com.mengcraft.ChestSQL;

public class Tools {
	 public String hexToText(String Hex) {
	    	int i;
	    	int HexLength;
	    	int CharIndex1;
	    	int CharIndex2;
	    	byte[] TextData;
	    	String HexChar1;
	    	String HexChar2;
	    	String ResultText = "";
	    	if(Hex != null) {
	    		HexLength = Hex.length();
	    		if(HexLength > 0) {
	    			if(HexLength % 2 == 0) {
	    				ResultText = "0123456789ABCDEF";
	    				HexLength /= 2;
	    				TextData = new byte[HexLength];
	    				for(i = 0; i < HexLength; i++) {
	    					HexChar1 = Hex.substring(i * 2, i * 2 + 1);
	    					HexChar2 = Hex.substring(i * 2 + 1, i * 2 + 2);
	    					CharIndex1 = ResultText.indexOf(HexChar1);
	    					CharIndex2 = ResultText.indexOf(HexChar2);
	    					TextData[i] = (byte)((CharIndex1 << 4) + CharIndex2);
	    				}
	    				ResultText = new String(TextData);
	    			}
	    		}
	    	}
	    	return ResultText;
	    }
	 
	 public String textToHex(String Text)
	    {
	    	int i;
	    	int TextLength;
	    	byte TextData[];
	    	String HexText = "";
	    	StringBuilder HexBuilder;
	    	if(Text != null) {
	    		TextLength = Text.length();
	    		if(TextLength > 0) {
	    			HexText = "0123456789ABCDEF";
	    			TextData = Text.getBytes();
	    			TextLength = TextData.length;
	    			if(TextLength <= 0) {
	    				HexText = "";
	    			}
	    			else
	    			{
	    				HexBuilder = new StringBuilder();
	    				for(i = 0; i < TextLength; i++) {
	    					HexBuilder.append(HexText.charAt((TextData[i] & 0xF0) >> 4));
	    					HexBuilder.append(HexText.charAt(TextData[i] & 0x0F));
	    				}
	    				HexText = HexBuilder.toString();
	    			}
	    		}
	    	}
	    	return HexText;
	    }
	 
	 private String[] textArrayAdd(String[] Array, String Text) {
	    	String[] ResultArray = null;
	    	int ArrayCount;
	    	if(Array != null && Text != null) {
	    		ArrayCount = Array.length;
	    		if(ArrayCount > 0) {
	    			ResultArray = new String[ArrayCount + 1];
	    			System.arraycopy(Array, 0, ResultArray, 0, ArrayCount);
	    			ResultArray[ArrayCount] = Text;
	    		}
	    		else {
	    			ResultArray = new String[1];
	    			ResultArray[0] = Text;
	    		}
	    	}
	    	return ResultArray;
	    }
	 
    public String[] SplitText(String Text, String Split) {
    	String[] ResultText = null;
    	int FindResult;
    	int TextLength;
    	int SplitLength;
    	String SplitText;
    	String TargetText;
    	if(Text != null && Split != null)
    	{
        	SplitLength = Split.length();
        	if(SplitLength > 0)
        	{
        		TargetText = Text;
        		ResultText = new String[0];
        		TextLength = TargetText.length();
        		if(TextLength > 0)
        		{
        			while(true)
        			{
        				FindResult = TargetText.indexOf(Split);
        				if(FindResult >= 0)
        				{
        					SplitText = TargetText.substring(0, FindResult);
        					TargetText = TargetText.substring(FindResult + SplitLength,TextLength);
        					ResultText = textArrayAdd(ResultText, SplitText);
        					TextLength = TargetText.length();
        				}
        				else
        				{
        					break;
        				}
        			}
        		}
        		ResultText = textArrayAdd(ResultText, TargetText);
        	}
    	}
    	return ResultText;
    }
    
    

}
