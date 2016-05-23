begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|CharArr
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableList
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableSet
import|;
end_import
begin_class
DECL|class|Utils
specifier|public
class|class
name|Utils
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getDeepCopy
specifier|public
specifier|static
name|Map
name|getDeepCopy
parameter_list|(
name|Map
name|map
parameter_list|,
name|int
name|maxDepth
parameter_list|)
block|{
return|return
name|getDeepCopy
argument_list|(
name|map
argument_list|,
name|maxDepth
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getDeepCopy
specifier|public
specifier|static
name|Map
name|getDeepCopy
parameter_list|(
name|Map
name|map
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|boolean
name|mutable
parameter_list|)
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|maxDepth
operator|<
literal|1
condition|)
return|return
name|map
return|;
name|Map
name|copy
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|Object
name|v
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|instanceof
name|Map
condition|)
name|v
operator|=
name|getDeepCopy
argument_list|(
operator|(
name|Map
operator|)
name|v
argument_list|,
name|maxDepth
operator|-
literal|1
argument_list|,
name|mutable
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|v
operator|instanceof
name|Collection
condition|)
name|v
operator|=
name|getDeepCopy
argument_list|(
operator|(
name|Collection
operator|)
name|v
argument_list|,
name|maxDepth
operator|-
literal|1
argument_list|,
name|mutable
argument_list|)
expr_stmt|;
name|copy
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
return|return
name|mutable
condition|?
name|copy
else|:
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|copy
argument_list|)
return|;
block|}
DECL|method|getDeepCopy
specifier|public
specifier|static
name|Collection
name|getDeepCopy
parameter_list|(
name|Collection
name|c
parameter_list|,
name|int
name|maxDepth
parameter_list|,
name|boolean
name|mutable
parameter_list|)
block|{
if|if
condition|(
name|c
operator|==
literal|null
operator|||
name|maxDepth
operator|<
literal|1
condition|)
return|return
name|c
return|;
name|Collection
name|result
init|=
name|c
operator|instanceof
name|Set
condition|?
operator|new
name|HashSet
argument_list|()
else|:
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|c
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|o
operator|=
name|getDeepCopy
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|,
name|maxDepth
operator|-
literal|1
argument_list|,
name|mutable
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|mutable
condition|?
name|result
else|:
name|result
operator|instanceof
name|Set
condition|?
name|unmodifiableSet
argument_list|(
operator|(
name|Set
operator|)
name|result
argument_list|)
else|:
name|unmodifiableList
argument_list|(
operator|(
name|List
operator|)
name|result
argument_list|)
return|;
block|}
DECL|method|toJSON
specifier|public
specifier|static
name|byte
index|[]
name|toJSON
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
operator|.
name|write
argument_list|(
name|o
argument_list|)
expr_stmt|;
comment|// indentation by default
return|return
name|toUTF8
argument_list|(
name|out
argument_list|)
return|;
block|}
DECL|method|toJSONString
specifier|public
specifier|static
name|String
name|toJSONString
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|toJSON
argument_list|(
name|o
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
DECL|method|toUTF8
specifier|public
specifier|static
name|byte
index|[]
name|toUTF8
parameter_list|(
name|CharArr
name|out
parameter_list|)
block|{
name|byte
index|[]
name|arr
init|=
operator|new
name|byte
index|[
name|out
operator|.
name|size
argument_list|()
operator|*
literal|3
index|]
decl_stmt|;
name|int
name|nBytes
init|=
name|ByteUtils
operator|.
name|UTF16toUTF8
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|nBytes
argument_list|)
return|;
block|}
DECL|method|fromJSON
specifier|public
specifier|static
name|Object
name|fromJSON
parameter_list|(
name|byte
index|[]
name|utf8
parameter_list|)
block|{
comment|// convert directly from bytes to chars
comment|// and parse directly from that instead of going through
comment|// intermediate strings or readers
name|CharArr
name|chars
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|ByteUtils
operator|.
name|UTF8toUTF16
argument_list|(
name|utf8
argument_list|,
literal|0
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|chars
argument_list|)
expr_stmt|;
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|chars
operator|.
name|getArray
argument_list|()
argument_list|,
name|chars
operator|.
name|getStart
argument_list|()
argument_list|,
name|chars
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// should never happen w/o using real IO
block|}
block|}
DECL|method|makeMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|makeMap
parameter_list|(
name|Object
modifier|...
name|keyVals
parameter_list|)
block|{
if|if
condition|(
operator|(
name|keyVals
operator|.
name|length
operator|&
literal|0x01
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"arguments should be key,value"
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|keyVals
operator|.
name|length
operator|>>
literal|1
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
name|keyVals
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|propMap
operator|.
name|put
argument_list|(
name|keyVals
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|keyVals
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|propMap
return|;
block|}
DECL|method|fromJSON
specifier|public
specifier|static
name|Object
name|fromJSON
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|ObjectBuilder
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|fromJSONResource
specifier|public
specifier|static
name|Object
name|fromJSONResource
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
return|return
name|fromJSON
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|resourceName
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fromJSONString
specifier|public
specifier|static
name|Object
name|fromJSONString
parameter_list|(
name|String
name|json
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|ObjectBuilder
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Parse error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getObjectByPath
specifier|public
specifier|static
name|Object
name|getObjectByPath
parameter_list|(
name|Map
name|root
parameter_list|,
name|boolean
name|onlyPrimitive
parameter_list|,
name|String
name|hierarchy
parameter_list|)
block|{
return|return
name|getObjectByPath
argument_list|(
name|root
argument_list|,
name|onlyPrimitive
argument_list|,
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|hierarchy
argument_list|,
literal|'/'
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getObjectByPath
specifier|public
specifier|static
name|Object
name|getObjectByPath
parameter_list|(
name|Map
name|root
parameter_list|,
name|boolean
name|onlyPrimitive
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
parameter_list|)
block|{
name|Map
name|obj
init|=
name|root
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
name|hierarchy
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|idx
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|s
init|=
name|hierarchy
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
name|Matcher
name|matcher
init|=
name|ARRAY_ELEMENT_INDEX
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|s
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|idx
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|i
operator|<
name|hierarchy
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|Object
name|o
init|=
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|idx
operator|>
operator|-
literal|1
condition|)
block|{
name|List
name|l
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
name|o
operator|=
name|idx
operator|<
name|l
operator|.
name|size
argument_list|()
condition|?
name|l
operator|.
name|get
argument_list|(
name|idx
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Map
operator|)
condition|)
return|return
literal|null
return|;
name|obj
operator|=
operator|(
name|Map
operator|)
name|o
expr_stmt|;
block|}
else|else
block|{
name|Object
name|val
init|=
name|obj
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
operator|-
literal|1
condition|)
block|{
name|List
name|l
init|=
operator|(
name|List
operator|)
name|val
decl_stmt|;
name|val
operator|=
name|idx
operator|<
name|l
operator|.
name|size
argument_list|()
condition|?
name|l
operator|.
name|get
argument_list|(
name|idx
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|onlyPrimitive
operator|&&
name|val
operator|instanceof
name|Map
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|val
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * If the passed entity has content, make sure it is fully    * read and closed.    *     * @param entity to consume or null    */
DECL|method|consumeFully
specifier|public
specifier|static
name|void
name|consumeFully
parameter_list|(
name|HttpEntity
name|entity
parameter_list|)
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// make sure the stream is full read
name|readFully
argument_list|(
name|entity
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// nothing to do then
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// quiet
block|}
finally|finally
block|{
comment|// close the stream
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Make sure the InputStream is fully read.    *     * @param is to read    * @throws IOException on problem with IO    */
DECL|method|readFully
specifier|private
specifier|static
name|void
name|readFully
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|is
operator|.
name|skip
argument_list|(
name|is
operator|.
name|available
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|is
operator|.
name|read
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{}
block|}
DECL|field|ARRAY_ELEMENT_INDEX
specifier|public
specifier|static
specifier|final
name|Pattern
name|ARRAY_ELEMENT_INDEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\S*?)\\[(\\d+)\\]"
argument_list|)
decl_stmt|;
block|}
end_class
end_unit
