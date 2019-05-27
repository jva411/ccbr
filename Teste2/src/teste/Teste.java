package teste;

import java.util.Random;

public class Teste {
    
    public static void main(String[] args) {
        
        int total = 128, soma = 9*total;
        Random rd = new Random();
        double r1 = (((double)rd.nextInt(800)/6000d)+0.52d), r2 = ((double)rd.nextInt(20)/1000d+0.0001d*total);
        System.out.println((int)(total/r1)+" "+(int)(total/r2));
        int soma2 = aa(soma-1, (int)(total/r1), (int)(total/r2));
        System.out.println(soma2);
        System.out.println(aa(soma-1, (int)(soma/(double)(rd.nextInt(6)/10d+0.8d)), (int)(soma/(double)(rd.nextInt(60)/100d+0.8d))));
        
    }
    
    private static int aa(int n, int N, int r){
        if(N>-1) n+= r;
        Random rd = new Random();
        int n1 = (n+1)*n/2;
        int n2 = rd.nextInt(n1+1);
        int n3 = 0;
        for(int n4=0;n4<n;n4++){
            for(int n5=0;n5<n-n4;n5++){
                if(n5+n3==n2) {
                    if(N>-1){
                        if(n4<r) return N + rd.nextInt(3)-1;
                        else return n4-r;
                    }
                    return n4;
                }
            }
            n3 += (n-n4);
        }
        if(N>-1) return n-r;
        return n;
    }
    
}