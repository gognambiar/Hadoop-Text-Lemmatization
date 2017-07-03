import java.io.IOException;
import java.util.*;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Lemma {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{

    //private final static IntWritable one = new IntWritable(1);
    private Text norm = new Text();
    private Text ld = new Text();
    MyMap hm = new MyMap();
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String inp = value.toString();
      String[] tinp = inp.split("\\t");
      if(tinp.length != 2)
      {
      return;
      }
      ld.set(tinp[0]);
      String pattern = "^[a-zA-Z0-9]+$";
      tinp[1] = tinp[1].toLowerCase();
      String[] a = tinp[1].split("\\s");
      for(int i=0;i<a.length-1;i++)
      {
      if(a[i].matches(pattern))
      {
      a[i] = a[i].replaceAll("j","i");
      a[i] = a[i].replaceAll("v","u");
      a[i] = a[i].replaceAll("\\p{P}","");
      //norm.set(a[i]);
      //context.write(norm,ld);
      String inp1 = hm.get(a[i]);
      if(inp1 != null)
      {
      String[] a1 = inp1.split("\\s");
      for(int nn = 0;nn<a1.length;nn++)
      {
      norm.set(a1[nn]);
      context.write(norm,ld);
      }
      }
      else
      {
      norm.set(a[i]);
      context.write(norm,ld);
      }
      }
      }
  }
}

public static class IntSumReducer
       extends Reducer<Text,Text,Text,Text> {
    private Text word = new Text();
    private Text lineid = new Text();
    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      String jj = key.toString();
      HashSet<String> st = new HashSet<String>();
      for (Text val : values) {
      String v1 = val.toString();
      st.add(v1);
}
String fe = st.toString();
word.set(jj);
lineid.set(fe);
context.write(word,lineid);
    }
  }


static class MyMap extends HashMap<String,String>{
    public MyMap() {
    try{  
      //---------------------Line reader
      String splitBy = ",";
      BufferedReader br = new BufferedReader(new FileReader("new_lemmatizer.csv"));
      String line1;
      while((line1 = br.readLine()) != null){
           String[] b = line1.split(splitBy);
           if(b.length == 2)
           this.put(b[0],b[1]);    
           if(b.length == 3)
           this.put(b[0],b[1]+" "+b[2]);
           if(b.length == 4)
           this.put(b[0],b[1]+" "+b[2]+" "+b[3]);
           if(b.length == 5)
           this.put(b[0],b[1]+" "+b[2]+" "+b[3]+" "+b[4]);
	   if(b.length == 6)
           this.put(b[0],b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]);
           if(b.length == 7)
           this.put(b[0],b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]);
           if(b.length == 8)
           this.put(b[0],b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]);
           if(b.length == 9)
           this.put(b[0],b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]+" "+b[8]);
           //System.out.println("Lemma is "+b[1]+" "+b[2]);
      }
      br.close();
//---------------------
}
catch(Exception e)
{
}
        }
    }


  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(Lemma.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}