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

public class mco {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{

    //private final static IntWritable one = new IntWritable(1);
    private Text norm = new Text();
    private Text ld = new Text();
    MyMap hm = new MyMap();
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      try
      {
      String pattern = "^[a-zA-Z]+$";
      String inp = value.toString();
      String[] tinp = inp.split(">");
      if(tinp.length != 2)
      {
      return;
      }
      tinp[1] = tinp[1].toLowerCase();
      String[] a = tinp[1].split("\\s");
      ld.set(tinp[0]);
      for(int i=0;i<a.length-2;i++)
      {
      if(a[i].matches(pattern))
      {
      a[i] = a[i].replaceAll("j","i");
      a[i] = a[i].replaceAll("v","u");
      a[i] = a[i].replaceAll("\\p{P}","");
      for(int j = i+1;j<a.length-1;j++)
      {
      if(a[j].matches(pattern))
      {
      a[j] = a[j].replaceAll("j","i");
      a[j] = a[j].replaceAll("v","u");
      a[j] = a[j].replaceAll("\\p{P}","");
      for(int k = j+1;k<a.length;k++)
      {
      if(a[k].matches(pattern))
      {
      a[k] = a[k].replaceAll("j","i");
      a[k] = a[k].replaceAll("v","u");
      a[k] = a[k].replaceAll("\\p{P}","");
      String iinp = hm.get(a[i]);
      String jinp = hm.get(a[j]);
      String kinp = hm.get(a[k]);
      String i1,j1,k1;
      if(hm.get(a[i]) != null)
      {
      	if(hm.get(a[j]) != null)
      	{
      		if(hm.get(a[k]) != null)
      		{
			String[] ai = iinp.split("\\s");
			String[] aj = jinp.split("\\s");
			String[] ak = kinp.split("\\s");
			for(int ll = 0;ll<ai.length;ll++)
			{
		                for(int mm = 0;mm<aj.length;mm++)
		                {
				        for(int nn = 0;nn<ak.length;nn++)
				        {
						norm.set(ai[ll]+" "+aj[mm]+" "+ak[nn]);
				        }
		                }
                        }

      		}//k
      		else
      		{
			String[] ai = iinp.split("\\s");
			String[] aj = jinp.split("\\s");
      			for(int ll = 0;ll<ai.length;ll++)
			{
		                for(int mm = 0;mm<aj.length;mm++)
		                {
					norm.set(ai[ll]+" "+aj[mm]+" "+a[k]);	
		                }
                        }
      		}
      	}//j
      	else
      	{
      		if(hm.get(a[k]) != null)
      		{
			String[] ai = iinp.split("\\s");
			String[] ak = kinp.split("\\s");
			for(int ll = 0;ll<ai.length;ll++)
			{
			        for(int nn = 0;nn<ak.length;nn++)
			        {
					norm.set(ai[ll]+" "+a[j]+" "+ak[nn]);
			        }
                        }      			
      		}//k
      		else
      		{
			String[] ai = iinp.split("\\s");
			for(int ll = 0;ll<ai.length;ll++)
			{
				norm.set(ai[ll]+" "+a[j]+" "+a[k]);
                        }
      		}
       	}
      }//i	
      else
      {
      	if(hm.get(a[j]) != null)
      	{
      		if(hm.get(a[k]) != null)
      		{
			String[] aj = jinp.split("\\s");
			String[] ak = kinp.split("\\s");

	                for(int mm = 0;mm<aj.length;mm++)
	                {
			        for(int nn = 0;nn<ak.length;nn++)
			        {
					norm.set(a[i]+" "+aj[mm]+" "+ak[nn]);
			        }
	                }

      		}//k
      		else
      		{
			String[] aj = jinp.split("\\s");
	                for(int mm = 0;mm<aj.length;mm++)
	                {
				norm.set(a[i]+" "+aj[mm]+" "+a[k]);	
	                }
      		}
      	}//j
      	else
      	{
      		if(hm.get(a[k]) != null)
      		{
			String[] ak = kinp.split("\\s");
		        for(int nn = 0;nn<ak.length;nn++)
		        {
				norm.set(a[i]+" "+a[j]+" "+ak[nn]);
		        }      			
      		}//k
      		else
      		{
			norm.set(a[i]+" "+a[j]+" "+a[k]);
      		}
       	}
      }

      context.write(norm,ld);
      }
      }//for
      }
      }//for
      }
      }//for
      }
      catch(ArrayIndexOutOfBoundsException exception)
      {
      exception.printStackTrace();
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
      //StringBuilder st = new StringBuilder();
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
    job.setJarByClass(mco.class);
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