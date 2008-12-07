begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
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
name|client
operator|.
name|solrj
operator|.
name|ResponseParser
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|BinaryResponseParser
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|XMLResponseParser
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestWriterPerf
specifier|public
class|class
name|TestWriterPerf
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema11.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-functionquery.xml"
return|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|field|id
name|String
name|id
init|=
literal|"id"
decl_stmt|;
DECL|field|t1
name|String
name|t1
init|=
literal|"f_t"
decl_stmt|;
DECL|field|i1
name|String
name|i1
init|=
literal|"f_i"
decl_stmt|;
DECL|field|tag
name|String
name|tag
init|=
literal|"f_ss"
decl_stmt|;
DECL|method|index
name|void
name|index
parameter_list|(
name|Object
modifier|...
name|olst
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|olst
control|)
name|lst
operator|.
name|add
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|lst
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|lst
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeIndex
name|void
name|makeIndex
parameter_list|()
block|{
name|index
argument_list|(
name|id
argument_list|,
literal|1
argument_list|,
name|i1
argument_list|,
literal|100
argument_list|,
name|t1
argument_list|,
literal|"now is the time for all good men"
argument_list|,
name|tag
argument_list|,
literal|"patriotic"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|2
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"to come to the aid of their country."
argument_list|,
name|tag
argument_list|,
literal|"patriotic"
argument_list|,
name|tag
argument_list|,
literal|"country"
argument_list|,
name|tag
argument_list|,
literal|"nation"
argument_list|,
name|tag
argument_list|,
literal|"speeches"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|3
argument_list|,
name|i1
argument_list|,
literal|2
argument_list|,
name|t1
argument_list|,
literal|"how now brown cow"
argument_list|,
name|tag
argument_list|,
literal|"cow"
argument_list|,
name|tag
argument_list|,
literal|"jersey"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|4
argument_list|,
name|i1
argument_list|,
operator|-
literal|100
argument_list|,
name|t1
argument_list|,
literal|"the quick fox jumped over the lazy dog"
argument_list|,
name|tag
argument_list|,
literal|"fox"
argument_list|,
name|tag
argument_list|,
literal|"dog"
argument_list|,
name|tag
argument_list|,
literal|"quick"
argument_list|,
name|tag
argument_list|,
literal|"slow"
argument_list|,
name|tag
argument_list|,
literal|"lazy"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|5
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"the quick fox jumped way over the lazy dog"
argument_list|,
name|tag
argument_list|,
literal|"fox"
argument_list|,
name|tag
argument_list|,
literal|"dog"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|6
argument_list|,
name|i1
argument_list|,
operator|-
literal|60
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|,
name|tag
argument_list|,
literal|"humpty"
argument_list|,
name|tag
argument_list|,
literal|"dumpty"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|7
argument_list|,
name|i1
argument_list|,
literal|123
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy had a great fall"
argument_list|,
name|tag
argument_list|,
literal|"accidents"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|8
argument_list|,
name|i1
argument_list|,
literal|876
argument_list|,
name|t1
argument_list|,
literal|"all the kings horses and all the kings men"
argument_list|,
name|tag
argument_list|,
literal|"king"
argument_list|,
name|tag
argument_list|,
literal|"horses"
argument_list|,
name|tag
argument_list|,
literal|"trouble"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|9
argument_list|,
name|i1
argument_list|,
literal|7
argument_list|,
name|t1
argument_list|,
literal|"couldn't put humpty together again"
argument_list|,
name|tag
argument_list|,
literal|"humpty"
argument_list|,
name|tag
argument_list|,
literal|"broken"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|10
argument_list|,
name|i1
argument_list|,
literal|4321
argument_list|,
name|t1
argument_list|,
literal|"this too shall pass"
argument_list|,
name|tag
argument_list|,
literal|"1"
argument_list|,
name|tag
argument_list|,
literal|"2"
argument_list|,
name|tag
argument_list|,
literal|"infinity"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|11
argument_list|,
name|i1
argument_list|,
literal|33
argument_list|,
name|t1
argument_list|,
literal|"An eye for eye only ends up making the whole world blind."
argument_list|,
name|tag
argument_list|,
literal|"ouch"
argument_list|,
name|tag
argument_list|,
literal|"eye"
argument_list|,
name|tag
argument_list|,
literal|"peace"
argument_list|,
name|tag
argument_list|,
literal|"world"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|12
argument_list|,
name|i1
argument_list|,
literal|379
argument_list|,
name|t1
argument_list|,
literal|"Great works are performed, not by strength, but by perseverance."
argument_list|,
name|tag
argument_list|,
literal|"herculese"
argument_list|,
name|tag
argument_list|,
literal|"strong"
argument_list|,
name|tag
argument_list|,
literal|"stubborn"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** make sure to close req after you are done using the response */
DECL|method|getResponse
specifier|public
name|SolrQueryResponse
name|getResponse
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|execute
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|null
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
return|return
name|rsp
return|;
block|}
DECL|method|doPerf
name|void
name|doPerf
parameter_list|(
name|String
name|writerName
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|int
name|encIter
parameter_list|,
name|int
name|decIter
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
name|getResponse
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|QueryResponseWriter
name|w
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
name|writerName
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
literal|null
decl_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|encIter
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|w
operator|instanceof
name|BinaryQueryResponseWriter
condition|)
block|{
name|BinaryQueryResponseWriter
name|binWriter
init|=
operator|(
name|BinaryQueryResponseWriter
operator|)
name|w
decl_stmt|;
name|out
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|binWriter
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|out
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
comment|// to be fair, from my previous tests, much of the performance will be sucked up
comment|// by java's UTF-8 encoding/decoding, not the actual writing
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|encodeTime
init|=
name|Math
operator|.
name|max
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|arr
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|writerName
operator|=
name|writerName
operator|.
name|intern
argument_list|()
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
name|decIter
condition|;
name|i
operator|++
control|)
block|{
name|ResponseParser
name|rp
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|writerName
operator|==
literal|"xml"
condition|)
block|{
name|rp
operator|=
operator|new
name|XMLResponseParser
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|writerName
operator|==
literal|"javabin"
condition|)
block|{
name|rp
operator|=
operator|new
name|BinaryResponseParser
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|arr
argument_list|)
decl_stmt|;
name|rp
operator|.
name|processResponse
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
name|long
name|decodeTime
init|=
name|Math
operator|.
name|max
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"writer "
operator|+
name|writerName
operator|+
literal|", size="
operator|+
name|out
operator|.
name|size
argument_list|()
operator|+
literal|", encodeRate="
operator|+
operator|(
name|encodeTime
operator|==
literal|1
condition|?
literal|"N/A"
else|:
literal|""
operator|+
operator|(
name|encIter
operator|*
literal|1000L
operator|/
name|encodeTime
operator|)
operator|)
operator|+
literal|", decodeRate="
operator|+
operator|(
name|decodeTime
operator|==
literal|1
condition|?
literal|"N/A"
else|:
literal|""
operator|+
operator|(
name|decIter
operator|*
literal|1000L
operator|/
name|decodeTime
operator|)
operator|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPerf
specifier|public
name|void
name|testPerf
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndex
argument_list|()
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[* TO *] all country"
argument_list|,
literal|"start"
argument_list|,
literal|"0"
argument_list|,
literal|"rows"
argument_list|,
literal|"100"
argument_list|,
literal|"echoParams"
argument_list|,
literal|"all"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"indent"
argument_list|,
literal|"false"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|i1
argument_list|,
literal|"facet.field"
argument_list|,
name|tag
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"100"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
literal|"t1"
argument_list|)
decl_stmt|;
comment|// just for testing
name|doPerf
argument_list|(
literal|"xml"
argument_list|,
name|req
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doPerf
argument_list|(
literal|"json"
argument_list|,
name|req
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doPerf
argument_list|(
literal|"javabin"
argument_list|,
name|req
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|int
name|encIter
init|=
literal|20000
decl_stmt|;
name|int
name|decIter
init|=
literal|50000
decl_stmt|;
comment|// warm up hotspot
comment|// doPerf("xml", req, 200,1000);
comment|// doPerf("json", req, 200,1000);
comment|// doPerf("javabin", req, 200,1000);
comment|// doPerf("xml", req, encIter, decIter);
comment|// doPerf("json", req, encIter, decIter);
comment|//doPerf("javabin", req, encIter, decIter);
comment|// doPerf("javabin", req, 1, decIter);
block|}
block|}
end_class
end_unit
