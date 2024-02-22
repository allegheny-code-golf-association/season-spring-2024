package com.golf.five;class Main{static void main(String[]s){int l=0,a=0;for(int y:s[0].toCharArray()){y-=48;l=a<9&y>9|l==1?1:y==40?l+10:l+y*(10-a);a++;}System.out.println(l%11<1&a==10);}}
