package me.jvagamer.cpu.com.inventory;

import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.api.ItemConstructor;
import me.jvagamer.cpu.com.SuperStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Sorter {
    
    private static ItemConstructor Icons = new ItemConstructor();
    
    public static ArrayList<SuperStack> sortByName(ArrayList<SuperStack> sss){
        ArrayList<SuperStack> SSs = new ArrayList<>();
        HashMap<String, SuperStack> tempMap = new HashMap<>();
        ArrayList<String> Names = new ArrayList<>();
        int i = 0;
        for(SuperStack ss:sss) {
            StringBuilder sb = new StringBuilder();
            IsBuilder ib = new IsBuilder(ss.getItem());
            sb.append(ib.getItemStack().getType().toString());
            if(ib.hasName()) sb.append('.').append(ib.getName());
            Names.add(sb.append('.').append(i).toString());
            tempMap.put(sb.toString(), ss);
            i++;
        }
        Collections.sort(Names);
        for(String a:Names) {
            SuperStack ss = tempMap.get(a);
            SSs.add(ss);
        }
        return SSs;
    }
    
//    public static ArrayList<SuperStack> sortByOldIds(ArrayList<SuperStack> sss){
//        ArrayList<SuperStack> SSs = new ArrayList<>();
//        HashMap<String, SuperStack> tempMap = new HashMap<>();
//        ArrayList<String> Names = new ArrayList<>();
//        return SSs;
//    }
    
}