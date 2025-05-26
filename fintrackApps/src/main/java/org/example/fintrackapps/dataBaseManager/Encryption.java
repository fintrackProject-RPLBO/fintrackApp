package org.example.fintrackapps.dataBaseManager;

public class Encryption {
    int shift;

    public Encryption(int shift){
        this.shift = shift;
    }

    public String encryption(String password){
        int forward = shift;

        if (forward < 0){
            forward *= -1;
        }

        char[]passArr = password.toCharArray();
        StringBuilder temp = new StringBuilder();

        for (char i : passArr){
            temp.append(hash(i,forward));
        }
        String result = (String) temp.toString();

        return result;
    }

    char hash(char inputChr, int forward){
        Character chr = (Character) inputChr;
        int hashcode = chr.hashCode();


        int code = Math.floorMod((hashcode+forward),128);
        char result = (char) code;

        return result;
    }

    public String decryption(String password){
        int backward = shift;

        if (backward > 0){
            backward *= -1;
        }

        char[]passArr = password.toCharArray();
        StringBuilder temp = new StringBuilder();

        for (char i : passArr){
            temp.append(hash(i,backward));
        }
        String result = (String) temp.toString();
        return result;
    }

}
