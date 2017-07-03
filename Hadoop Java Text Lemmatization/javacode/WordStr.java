import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import java.util.Set;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordStr {

public static class TokenizerMapper extends Mapper<Object,Text,Text,MyMap>
{
    
    private MyMap m11 = new MyMap();
    private Text word = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String pattern = "^[a-zA-Z]+$";
      String[] tokens = value.toString().split("\\W+");
      if(tokens.length > 1)
      {
      for(int i = 0;i<tokens.length-1;i++)
      {
      if(tokens[i].matches(pattern))
      {
      word.set(tokens[i]);
      m11.clear();

      for(int j=i+1;j<tokens.length;j++)
      {
      if(tokens[j].matches(pattern))
      {
      if(m11.containsKey(tokens[j]))
      {
      IntWritable count = (IntWritable)m11.get(tokens[j]);
      count.set(count.get()+1);
      }
      else
      {
      Text n = new Text(tokens[j]);
      m11.put(n,new IntWritable(1));
      }
      }
      }//for
      context.write(word, m11);
    }
  }//for
  }
}
}

  public static class IntSumReducer
       extends Reducer<Text,MyMap,Text,MyMap> {
    private MyMap m22 = new MyMap();
    public void reduce(Text key, Iterable<MyMap> values,
                       Context context
                       ) throws IOException, InterruptedException {
      m22.clear();
      for(MyMap value : values)
      {
      SumVal(value);
      }
      context.write(key,m22);
    }

    private void SumVal(MyMap MyMap)
    {
    Set<Writable> keys = MyMap.keySet();
    for(Writable key : keys)
    {
    IntWritable fromCount = (IntWritable) MyMap.get(key);
    if(m22.containsKey(key))
    {
    IntWritable count = (IntWritable) m22.get(key);
    count.set(count.get() + fromCount.get());
    }
    else
    {
    m22.put(key,fromCount);
    }
    }
    }
  }

static class MyMap extends MapWritable {
    public String toString() {
        StringBuilder result = new StringBuilder();
        Set<Writable> keySet = this.keySet();

        for (Object key : keySet) {
            result.append("{" + key.toString() + " -> " + this.get(key) + "}");
        }
        return result.toString();
    }
}

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordStr.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(MyMap.class);
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(MyMap.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}