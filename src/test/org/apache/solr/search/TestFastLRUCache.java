begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ConcurrentLRUCache
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**  * Test for FastLRUCache  *  * @version $Id$  * @see org.apache.solr.search.FastLRUCache  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestFastLRUCache
specifier|public
class|class
name|TestFastLRUCache
extends|extends
name|TestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|FastLRUCache
name|sc
init|=
operator|new
name|FastLRUCache
argument_list|()
decl_stmt|;
name|Map
name|l
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|l
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|l
operator|.
name|put
argument_list|(
literal|"initialSize"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|l
operator|.
name|put
argument_list|(
literal|"autowarmCount"
argument_list|,
literal|"25"
argument_list|)
expr_stmt|;
name|CacheRegenerator
name|cr
init|=
operator|new
name|CacheRegenerator
argument_list|()
block|{
specifier|public
name|boolean
name|regenerateItem
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrCache
name|newCache
parameter_list|,
name|SolrCache
name|oldCache
parameter_list|,
name|Object
name|oldKey
parameter_list|,
name|Object
name|oldVal
parameter_list|)
throws|throws
name|IOException
block|{
name|newCache
operator|.
name|put
argument_list|(
name|oldKey
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|Object
name|o
init|=
name|sc
operator|.
name|init
argument_list|(
name|l
argument_list|,
literal|null
argument_list|,
name|cr
argument_list|)
decl_stmt|;
name|sc
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|101
condition|;
name|i
operator|++
control|)
block|{
name|sc
operator|.
name|put
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"25"
argument_list|,
name|sc
operator|.
name|get
argument_list|(
literal|25
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|sc
operator|.
name|get
argument_list|(
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
name|sc
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"lookups"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|101L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|sc
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// first item put in should be the first out
name|FastLRUCache
name|scNew
init|=
operator|new
name|FastLRUCache
argument_list|()
decl_stmt|;
name|scNew
operator|.
name|init
argument_list|(
name|l
argument_list|,
name|o
argument_list|,
name|cr
argument_list|)
expr_stmt|;
name|scNew
operator|.
name|warm
argument_list|(
literal|null
argument_list|,
name|sc
argument_list|)
expr_stmt|;
name|scNew
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|scNew
operator|.
name|put
argument_list|(
literal|103
argument_list|,
literal|"103"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"90"
argument_list|,
name|scNew
operator|.
name|get
argument_list|(
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|scNew
operator|.
name|get
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|nl
operator|=
name|scNew
operator|.
name|getStatistics
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"lookups"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"evictions"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"cumulative_lookups"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"cumulative_hits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|102L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"cumulative_inserts"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOldestItems
specifier|public
name|void
name|testOldestItems
parameter_list|()
block|{
name|ConcurrentLRUCache
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|cache
init|=
operator|new
name|ConcurrentLRUCache
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|(
literal|100
argument_list|,
literal|90
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cache
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|m
init|=
name|cache
operator|.
name|getOldestAccessedItems
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|//7 6 5 4 2
name|assertNotNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doPerfTest
name|void
name|doPerfTest
parameter_list|(
name|int
name|iter
parameter_list|,
name|int
name|cacheSize
parameter_list|,
name|int
name|maxKey
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|lowerWaterMark
init|=
name|cacheSize
decl_stmt|;
name|int
name|upperWaterMark
init|=
call|(
name|int
call|)
argument_list|(
name|lowerWaterMark
operator|*
literal|1.1
argument_list|)
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ConcurrentLRUCache
name|cache
init|=
operator|new
name|ConcurrentLRUCache
argument_list|(
name|upperWaterMark
argument_list|,
name|lowerWaterMark
argument_list|,
operator|(
name|upperWaterMark
operator|+
name|lowerWaterMark
operator|)
operator|/
literal|2
argument_list|,
name|upperWaterMark
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|boolean
name|getSize
init|=
literal|false
decl_stmt|;
name|int
name|minSize
init|=
literal|0
decl_stmt|,
name|maxSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxKey
argument_list|)
argument_list|,
literal|"TheValue"
argument_list|)
expr_stmt|;
name|int
name|sz
init|=
name|cache
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|getSize
operator|&&
name|sz
operator|>=
name|cacheSize
condition|)
block|{
name|getSize
operator|=
literal|true
expr_stmt|;
name|minSize
operator|=
name|sz
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|sz
operator|<
name|minSize
condition|)
name|minSize
operator|=
name|sz
expr_stmt|;
elseif|else
if|if
condition|(
name|sz
operator|>
name|maxSize
condition|)
name|maxSize
operator|=
name|sz
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"time="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|", minSize="
operator|+
name|minSize
operator|+
literal|",maxSize="
operator|+
name|maxSize
argument_list|)
expr_stmt|;
block|}
comment|/***   public void testPerf() {     doPerfTest(1000000, 100000, 200000); // big cache, warmup     doPerfTest(2000000, 100000, 200000); // big cache     doPerfTest(2000000, 100000, 120000);  // smaller key space increases distance between oldest, newest and makes the first passes less effective.     doPerfTest(6000000, 1000, 2000);    // small cache, smaller hit rate     doPerfTest(6000000, 1000, 1200);    // small cache, bigger hit rate   }   ***/
comment|// returns number of puts
DECL|method|useCache
name|int
name|useCache
parameter_list|(
name|SolrCache
name|sc
parameter_list|,
name|int
name|numGets
parameter_list|,
name|int
name|maxKey
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
comment|// use like a cache... gets and a put if not found
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numGets
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|k
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxKey
argument_list|)
decl_stmt|;
name|Integer
name|v
init|=
operator|(
name|Integer
operator|)
name|sc
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|sc
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|ret
operator|++
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|fillCache
name|void
name|fillCache
parameter_list|(
name|SolrCache
name|sc
parameter_list|,
name|int
name|cacheSize
parameter_list|,
name|int
name|maxKey
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cacheSize
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|kv
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxKey
argument_list|)
decl_stmt|;
name|sc
operator|.
name|put
argument_list|(
name|kv
argument_list|,
name|kv
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cachePerfTest
name|void
name|cachePerfTest
parameter_list|(
specifier|final
name|SolrCache
name|sc
parameter_list|,
specifier|final
name|int
name|nThreads
parameter_list|,
specifier|final
name|int
name|numGets
parameter_list|,
name|int
name|cacheSize
parameter_list|,
specifier|final
name|int
name|maxKey
parameter_list|)
block|{
name|Map
name|l
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|l
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|""
operator|+
name|cacheSize
argument_list|)
expr_stmt|;
name|l
operator|.
name|put
argument_list|(
literal|"initialSize"
argument_list|,
literal|""
operator|+
name|cacheSize
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|sc
operator|.
name|init
argument_list|(
name|l
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|sc
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|fillCache
argument_list|(
name|sc
argument_list|,
name|cacheSize
argument_list|,
name|maxKey
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|nThreads
index|]
decl_stmt|;
specifier|final
name|AtomicInteger
name|puts
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|seed
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|ret
init|=
name|useCache
argument_list|(
name|sc
argument_list|,
name|numGets
operator|/
name|nThreads
argument_list|,
name|maxKey
argument_list|,
name|seed
argument_list|)
decl_stmt|;
name|puts
operator|.
name|addAndGet
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
try|try
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
try|try
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"time="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
operator|+
literal|" impl="
operator|+
name|sc
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" nThreads= "
operator|+
name|nThreads
operator|+
literal|" size="
operator|+
name|cacheSize
operator|+
literal|" maxKey="
operator|+
name|maxKey
operator|+
literal|" gets="
operator|+
name|numGets
operator|+
literal|" hitRatio="
operator|+
operator|(
literal|1
operator|-
operator|(
operator|(
operator|(
name|double
operator|)
name|puts
operator|.
name|get
argument_list|()
operator|)
operator|/
name|numGets
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|perfTestBoth
name|void
name|perfTestBoth
parameter_list|(
name|int
name|nThreads
parameter_list|,
name|int
name|numGets
parameter_list|,
name|int
name|cacheSize
parameter_list|,
name|int
name|maxKey
parameter_list|)
block|{
name|cachePerfTest
argument_list|(
operator|new
name|LRUCache
argument_list|()
argument_list|,
name|nThreads
argument_list|,
name|numGets
argument_list|,
name|cacheSize
argument_list|,
name|maxKey
argument_list|)
expr_stmt|;
name|cachePerfTest
argument_list|(
operator|new
name|FastLRUCache
argument_list|()
argument_list|,
name|nThreads
argument_list|,
name|numGets
argument_list|,
name|cacheSize
argument_list|,
name|maxKey
argument_list|)
expr_stmt|;
block|}
comment|/***   public void testCachePerf() {     // warmup     perfTestBoth(2, 100000, 100000, 120000);     perfTestBoth(1, 2000000, 100000, 100000); // big cache, 100% hit ratio     perfTestBoth(2, 2000000, 100000, 100000); // big cache, 100% hit ratio     perfTestBoth(1, 2000000, 100000, 120000); // big cache, bigger hit ratio     perfTestBoth(2, 2000000, 100000, 120000); // big cache, bigger hit ratio     perfTestBoth(1, 2000000, 100000, 200000); // big cache, ~50% hit ratio     perfTestBoth(2, 2000000, 100000, 200000); // big cache, ~50% hit ratio     perfTestBoth(1, 2000000, 100000, 1000000); // big cache, ~10% hit ratio     perfTestBoth(2, 2000000, 100000, 1000000); // big cache, ~10% hit ratio      perfTestBoth(1, 2000000, 1000, 1000); // small cache, ~100% hit ratio     perfTestBoth(2, 2000000, 1000, 1000); // small cache, ~100% hit ratio     perfTestBoth(1, 2000000, 1000, 1200); // small cache, bigger hit ratio     perfTestBoth(2, 2000000, 1000, 1200); // small cache, bigger hit ratio     perfTestBoth(1, 2000000, 1000, 2000); // small cache, ~50% hit ratio     perfTestBoth(2, 2000000, 1000, 2000); // small cache, ~50% hit ratio     perfTestBoth(1, 2000000, 1000, 10000); // small cache, ~10% hit ratio     perfTestBoth(2, 2000000, 1000, 10000); // small cache, ~10% hit ratio   }   ***/
block|}
end_class
end_unit
