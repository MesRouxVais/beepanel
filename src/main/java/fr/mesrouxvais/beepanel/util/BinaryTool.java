package fr.mesrouxvais.beepanel.util;

public class BinaryTool {
	
	
	
	/**
     * Isole les bits d'un nombre entre les positions x et y (incluses)
     * @param number Le nombre dont on veut extraire les bits
     * @param x La position de début (en partant de la droite, 0-based)
     * @param y La position de fin (en partant de la droite, 0-based)
     * @return Le nombre formé par les bits isolés
     */
    public static int isolateBits(int number, int x, int y) {
        // Vérifie que x est inférieur à y
        if (x > y) {
            throw new IllegalArgumentException("x doit être inférieur ou égal à y");
        }
        
        // Crée un masque de la longueur appropriée
        int length = y - x + 1;
        int mask = ((1 << length) - 1) << x;
        
        // Applique le masque et décale le résultat
        return (number & mask) >>> x;
    }
    
    public static byte[] stringHexaToArray(String hexValue) {
    	
    	if (hexValue.length() % 2 != 0) {
            throw new IllegalArgumentException("La chaîne hexadécimale doit avoir une longueur paire.");
        }
		
    	byte[] byteArray = new byte[hexValue.length() / 2];
        
        for (int i = 0; i < hexValue.length(); i += 2) {
            int intValue = Integer.parseInt(hexValue.substring(i, i + 2), 16);
            byteArray[i / 2] = (byte) intValue;
        }
        
        return byteArray;
    	
    }
	
	
	public static byte[] invertArray(byte[] array) {
    	
    	byte[] bufferArray = new byte[array.length];
    	
    	for(int i = array.length - 1; i>=0; i--) {
    		
    		
    		bufferArray[array.length -1-i] = array[i];
    	}
    	
    	return bufferArray;
    }
	
	//de 1 à x de gauche à droite 
	public static int extractBits(byte[] bytes, int startBit, int endBit) {
    	//isolé le system de byte 
    	int result = 0;
    	int firstByte = (startBit-1)/8;
    	if(firstByte <0 )firstByte = 0;

    	int endByte = (endBit-1)/8;
    	
    	int byteToCopy = 1 + endByte - firstByte;
    	
    	if(endByte >= bytes.length) {
    		throw new IllegalArgumentException(firstByte +"endBit out of array : " + endByte);
    	}
    	
    	for(int i = byteToCopy; i > 0; i--) {
    		System.out.println("byte To Copy : " + (byteToCopy-i) +"\t byte being copied : " + (i - 1 + firstByte));
    		result+= bytes[i - 1 + firstByte] *Math.pow(2, 8*(byteToCopy-i));
    		
    	}
    	
    	//isolé dans le int copier le bit relativement
    	
    	int relativePosition = startBit-firstByte*8;
    	int relativePositionEnd = endBit-firstByte*8;
    	System.out.println("Relative postion : " + relativePosition +" , " + relativePositionEnd);
    	
    	
        return isolateBits(result, (byteToCopy*8)-relativePositionEnd, (byteToCopy*8)-relativePosition);
    }
    
}
