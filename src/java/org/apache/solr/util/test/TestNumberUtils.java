begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util.test
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|test
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
name|NumberUtils
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
name|util
operator|.
name|BCDUtils
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
begin_comment
comment|/**  * @author yonik  */
end_comment
begin_class
DECL|class|TestNumberUtils
specifier|public
class|class
name|TestNumberUtils
block|{
DECL|method|arrstr
specifier|private
specifier|static
name|String
name|arrstr
parameter_list|(
name|char
index|[]
name|arr
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|String
name|str
init|=
literal|"["
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|str
operator|+=
name|arr
index|[
name|i
index|]
operator|+
literal|"("
operator|+
operator|(
name|int
operator|)
name|arr
index|[
name|i
index|]
operator|+
literal|"),"
expr_stmt|;
return|return
name|str
operator|+
literal|"]"
return|;
block|}
DECL|field|rng
specifier|static
name|Random
name|rng
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|special
specifier|static
name|int
index|[]
name|special
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|100
block|,
literal|1000
block|,
literal|10000
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
name|Integer
operator|.
name|MIN_VALUE
block|}
decl_stmt|;
DECL|method|getSpecial
specifier|static
name|int
name|getSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|int
name|j
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|j
return|;
return|return
name|special
index|[
operator|(
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|special
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|field|lspecial
specifier|static
name|long
index|[]
name|lspecial
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|100
block|,
literal|1000
block|,
literal|10000
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|256
block|,
literal|16384
block|,
literal|32768
block|,
literal|65536
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
name|Integer
operator|.
name|MIN_VALUE
block|,
name|Long
operator|.
name|MAX_VALUE
block|,
name|Long
operator|.
name|MIN_VALUE
block|}
decl_stmt|;
DECL|method|getLongSpecial
specifier|static
name|long
name|getLongSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|j
init|=
name|rng
operator|.
name|nextLong
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|j
return|;
return|return
name|lspecial
index|[
operator|(
operator|(
name|int
operator|)
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|special
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|field|fspecial
specifier|static
name|float
index|[]
name|fspecial
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|256
block|,
literal|16384
block|,
literal|32768
block|,
literal|65536
block|,
literal|.1f
block|,
literal|.25f
block|,
name|Float
operator|.
name|NEGATIVE_INFINITY
block|,
name|Float
operator|.
name|POSITIVE_INFINITY
block|,
name|Float
operator|.
name|MIN_VALUE
block|,
name|Float
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
DECL|method|getFloatSpecial
specifier|static
name|float
name|getFloatSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|int
name|j
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|float
name|f
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
name|f
condition|)
name|f
operator|=
literal|0
expr_stmt|;
comment|// get rid of NaN for comparison purposes
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|f
return|;
return|return
name|fspecial
index|[
operator|(
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|fspecial
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|field|dspecial
specifier|static
name|double
index|[]
name|dspecial
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|256
block|,
literal|16384
block|,
literal|32768
block|,
literal|65536
block|,
literal|.1
block|,
literal|.25
block|,
name|Float
operator|.
name|NEGATIVE_INFINITY
block|,
name|Float
operator|.
name|POSITIVE_INFINITY
block|,
name|Float
operator|.
name|MIN_VALUE
block|,
name|Float
operator|.
name|MAX_VALUE
block|,
name|Double
operator|.
name|NEGATIVE_INFINITY
block|,
name|Double
operator|.
name|POSITIVE_INFINITY
block|,
name|Double
operator|.
name|MIN_VALUE
block|,
name|Double
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
DECL|method|getDoubleSpecial
specifier|static
name|double
name|getDoubleSpecial
parameter_list|()
block|{
name|int
name|i
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|j
init|=
name|rng
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|double
name|f
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
name|f
condition|)
name|f
operator|=
literal|0
expr_stmt|;
comment|// get rid of NaN for comparison purposes
if|if
condition|(
operator|(
name|i
operator|&
literal|0x10
operator|)
operator|!=
literal|0
condition|)
return|return
name|f
return|;
return|return
name|dspecial
index|[
operator|(
operator|(
name|int
operator|)
name|j
operator|&
literal|0x7fffffff
operator|)
operator|%
name|dspecial
operator|.
name|length
index|]
operator|*
operator|(
operator|(
name|i
operator|&
literal|0x20
operator|)
operator|==
literal|0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|+
operator|(
operator|(
name|i
operator|&
literal|0x03
operator|)
operator|-
literal|1
operator|)
return|;
block|}
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|(
name|Comparable
name|n1
parameter_list|,
name|Comparable
name|n2
parameter_list|,
name|Converter
name|conv
parameter_list|)
block|{
name|String
name|s1
init|=
name|n1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|n2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|v1
init|=
name|conv
operator|.
name|toInternal
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|String
name|v2
init|=
name|conv
operator|.
name|toInternal
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|String
name|out1
init|=
name|conv
operator|.
name|toExternal
argument_list|(
name|v1
argument_list|)
decl_stmt|;
name|String
name|out2
init|=
name|conv
operator|.
name|toExternal
argument_list|(
name|v2
argument_list|)
decl_stmt|;
name|int
name|c1
init|=
name|n1
operator|.
name|compareTo
argument_list|(
name|n2
argument_list|)
decl_stmt|;
name|int
name|c2
init|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
decl_stmt|;
if|if
condition|(
name|c1
operator|==
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|==
literal|0
operator|)
operator|||
name|c1
argument_list|<
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|<
literal|0
operator|)
operator|||
name|c1
argument_list|>
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|>
literal|0
operator|)
operator|||
operator|!
name|out1
operator|.
name|equals
argument_list|(
name|s1
argument_list|)
operator|||
operator|!
name|out2
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Comparison error:"
operator|+
name|s1
operator|+
literal|","
operator|+
name|s2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"v1="
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|v1
operator|.
name|length
argument_list|()
condition|;
name|ii
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
operator|(
name|int
operator|)
name|v1
operator|.
name|charAt
argument_list|(
name|ii
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"\nv2="
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|v2
operator|.
name|length
argument_list|()
condition|;
name|ii
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
operator|(
name|int
operator|)
name|v2
operator|.
name|charAt
argument_list|(
name|ii
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nout1='"
operator|+
name|out1
operator|+
literal|"', out2='"
operator|+
name|out2
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
block|{
name|int
name|iter
init|=
literal|1000000
decl_stmt|;
name|int
name|arrsz
init|=
literal|100000
decl_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
name|String
name|test
init|=
literal|"b100"
decl_stmt|;
name|String
name|clazz
init|=
literal|"NoClass"
decl_stmt|;
for|for
control|(
name|int
name|argnum
init|=
literal|0
init|;
name|argnum
operator|<
name|args
operator|.
name|length
condition|;
name|argnum
operator|++
control|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|argnum
index|]
decl_stmt|;
if|if
condition|(
literal|"-t"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|test
operator|=
name|args
index|[
operator|++
name|argnum
index|]
expr_stmt|;
block|}
if|if
condition|(
literal|"-i"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|iter
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|argnum
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"-a"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|arrsz
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|argnum
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"-c"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|clazz
operator|=
name|args
index|[
operator|++
name|argnum
index|]
expr_stmt|;
block|}
if|if
condition|(
literal|"-r"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|rng
operator|.
name|setSeed
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
operator|++
name|argnum
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
if|if
condition|(
literal|"-n"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|num
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|argnum
index|]
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
comment|// Converter conv = (Converter)(Class.forName(clazz).newInstance());
name|Class
name|cls
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cls
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|cls
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"solr.util.test."
operator|+
name|clazz
argument_list|)
expr_stmt|;
block|}
name|Converter
name|conv
init|=
operator|(
name|Converter
operator|)
name|cls
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"ispecial"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
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
name|Integer
name|i1
init|=
name|getSpecial
argument_list|()
decl_stmt|;
name|Integer
name|i2
init|=
name|getSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|i1
argument_list|,
name|i2
argument_list|,
name|conv
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"lspecial"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
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
name|Long
name|f1
init|=
name|getLongSpecial
argument_list|()
decl_stmt|;
name|Long
name|f2
init|=
name|getLongSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|,
name|conv
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"fspecial"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
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
name|Float
name|f1
init|=
name|getFloatSpecial
argument_list|()
decl_stmt|;
name|Float
name|f2
init|=
name|getFloatSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|,
name|conv
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"dspecial"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
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
name|Double
name|f1
init|=
name|getDoubleSpecial
argument_list|()
decl_stmt|;
name|Double
name|f2
init|=
name|getDoubleSpecial
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|,
name|conv
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"10kout"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
name|String
name|n
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|num
argument_list|)
decl_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|n
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|char
index|[]
name|arr2
init|=
operator|new
name|char
index|[
name|n
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|n
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|n
operator|.
name|length
argument_list|()
argument_list|,
name|arr
argument_list|,
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|ret
operator|+=
name|BCDUtils
operator|.
name|base10toBase100SortableInt
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|,
name|arr2
argument_list|,
name|arr2
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"internal"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
operator|||
literal|"external"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
name|int
name|min
init|=
operator|-
literal|1000000
decl_stmt|;
name|int
name|max
init|=
literal|1000000
decl_stmt|;
name|String
index|[]
name|arr
init|=
operator|new
name|String
index|[
name|arrsz
index|]
decl_stmt|;
name|String
index|[]
name|internal
init|=
operator|new
name|String
index|[
name|arrsz
index|]
decl_stmt|;
if|if
condition|(
literal|"external"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arrsz
condition|;
name|i
operator|++
control|)
block|{
name|int
name|val
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
comment|// todo - move to between min and max...
name|arr
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|internal
index|[
name|i
index|]
operator|=
name|conv
operator|.
name|toInternal
argument_list|(
name|arr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
name|int
name|slot
init|=
name|i
operator|%
name|arrsz
decl_stmt|;
name|arr
index|[
name|slot
index|]
operator|=
name|conv
operator|.
name|toExternal
argument_list|(
name|internal
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|arr
index|[
name|slot
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arrsz
condition|;
name|i
operator|++
control|)
block|{
name|int
name|val
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
comment|// todo - move to between min and max...
name|arr
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|int
name|slot
init|=
name|i
operator|%
name|arrsz
decl_stmt|;
name|internal
index|[
name|slot
index|]
operator|=
name|conv
operator|.
name|toInternal
argument_list|(
name|arr
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|internal
index|[
name|slot
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"itest"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
operator|||
literal|"ltest"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
operator|||
literal|"ftest"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
name|long
name|internalLen
init|=
literal|0
decl_stmt|;
name|long
name|externalLen
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
name|Comparable
name|n1
init|=
literal|null
decl_stmt|,
name|n2
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"itest"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
name|Integer
name|i1
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|Integer
name|i2
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
comment|// concentrate on small numbers for a while
comment|// to try and hit boundary cases 0,1,-1,100,-100,etc
if|if
condition|(
name|i
operator|<
literal|10000
condition|)
block|{
name|i1
operator|=
operator|(
name|i1
operator|%
literal|250
operator|)
operator|-
literal|125
expr_stmt|;
name|i2
operator|=
operator|(
name|i2
operator|%
literal|250
operator|)
operator|-
literal|125
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
literal|500000
condition|)
block|{
name|i1
operator|=
operator|(
name|i1
operator|%
literal|25000
operator|)
operator|-
literal|12500
expr_stmt|;
name|i2
operator|=
operator|(
name|i2
operator|%
literal|25000
operator|)
operator|-
literal|12500
expr_stmt|;
block|}
name|n1
operator|=
name|i1
expr_stmt|;
name|n2
operator|=
name|i2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ltest"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
name|Long
name|i1
init|=
name|rng
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|Long
name|i2
init|=
name|rng
operator|.
name|nextLong
argument_list|()
decl_stmt|;
comment|// concentrate on small numbers for a while
comment|// to try and hit boundary cases 0,1,-1,100,-100,etc
if|if
condition|(
name|i
operator|<
literal|10000
condition|)
block|{
name|i1
operator|=
call|(
name|long
call|)
argument_list|(
name|i1
operator|%
literal|250
argument_list|)
operator|-
literal|125
expr_stmt|;
name|i2
operator|=
call|(
name|long
call|)
argument_list|(
name|i2
operator|%
literal|250
argument_list|)
operator|-
literal|125
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
literal|500000
condition|)
block|{
name|i1
operator|=
call|(
name|long
call|)
argument_list|(
name|i1
operator|%
literal|25000
argument_list|)
operator|-
literal|12500
expr_stmt|;
name|i2
operator|=
call|(
name|long
call|)
argument_list|(
name|i2
operator|%
literal|25000
argument_list|)
operator|-
literal|12500
expr_stmt|;
block|}
name|n1
operator|=
name|i1
expr_stmt|;
name|n2
operator|=
name|i2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ftest"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
name|Float
name|i1
decl_stmt|;
name|Float
name|i2
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|10000
condition|)
block|{
name|i1
operator|=
call|(
name|float
call|)
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
operator|%
literal|250
argument_list|)
operator|-
literal|125
expr_stmt|;
name|i2
operator|=
call|(
name|float
call|)
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
operator|%
literal|250
argument_list|)
operator|-
literal|125
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
literal|300000
condition|)
block|{
name|i1
operator|=
call|(
name|float
call|)
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
operator|%
literal|2500
argument_list|)
operator|-
literal|1250
expr_stmt|;
name|i2
operator|=
call|(
name|float
call|)
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
operator|%
literal|2500
argument_list|)
operator|-
literal|1250
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|<
literal|500000
condition|)
block|{
name|i1
operator|=
name|rng
operator|.
name|nextFloat
argument_list|()
operator|/
name|rng
operator|.
name|nextFloat
argument_list|()
expr_stmt|;
name|i2
operator|=
name|rng
operator|.
name|nextFloat
argument_list|()
operator|/
name|rng
operator|.
name|nextFloat
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|i1
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|i2
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|rng
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|n1
operator|=
name|i1
expr_stmt|;
name|n2
operator|=
name|i2
expr_stmt|;
block|}
name|String
name|s1
init|=
name|n1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|n2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|v1
init|=
name|conv
operator|.
name|toInternal
argument_list|(
name|s1
argument_list|)
decl_stmt|;
name|String
name|v2
init|=
name|conv
operator|.
name|toInternal
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|String
name|out1
init|=
name|conv
operator|.
name|toExternal
argument_list|(
name|v1
argument_list|)
decl_stmt|;
name|String
name|out2
init|=
name|conv
operator|.
name|toExternal
argument_list|(
name|v2
argument_list|)
decl_stmt|;
name|externalLen
operator|+=
name|s1
operator|.
name|length
argument_list|()
expr_stmt|;
name|internalLen
operator|+=
name|v1
operator|.
name|length
argument_list|()
expr_stmt|;
name|int
name|c1
init|=
name|n1
operator|.
name|compareTo
argument_list|(
name|n2
argument_list|)
decl_stmt|;
name|int
name|c2
init|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
decl_stmt|;
if|if
condition|(
name|c1
operator|==
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|==
literal|0
operator|)
operator|||
name|c1
argument_list|<
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|<
literal|0
operator|)
operator|||
name|c1
argument_list|>
literal|0
operator|&&
operator|!
operator|(
name|c2
operator|>
literal|0
operator|)
operator|||
operator|!
name|out1
operator|.
name|equals
argument_list|(
name|s1
argument_list|)
operator|||
operator|!
name|out2
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Comparison error:"
operator|+
name|s1
operator|+
literal|","
operator|+
name|s2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"v1="
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|v1
operator|.
name|length
argument_list|()
condition|;
name|ii
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
operator|(
name|int
operator|)
name|v1
operator|.
name|charAt
argument_list|(
name|ii
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"\nv2="
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|v2
operator|.
name|length
argument_list|()
condition|;
name|ii
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
operator|(
name|int
operator|)
name|v2
operator|.
name|charAt
argument_list|(
name|ii
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nout1='"
operator|+
name|out1
operator|+
literal|"', out2='"
operator|+
name|out2
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/******************     int sz=20;     char[] arr1 = new char[sz];     char[] arr2 = new char[sz];     char[] arr3 = new char[sz];     if ("noconv".equals(test)) {       for (int i=0; i<iter; i++) {         int val = rng.nextInt();         String istr = Integer.toString(val);         int n = istr.length();         Integer.toString(val).getChars(0, n, arr1, 0);         String nStr = new String(arr1,0,n);         if (!nStr.equals(istr)) {           System.out.println("ERROR! input="+istr+" output="+nStr);           System.out.println(arrstr(arr1,0,n));         }       }     } else if ("b100".equals(test)) {       for (int i=0; i<iter; i++) {         int val = rng.nextInt();         String istr = Integer.toString(val);         int n = istr.length();         Integer.toString(val).getChars(0, n, arr1, 0);          int b100_start = NumberUtils.base10toBase100(arr1,0,n,arr2,sz);         int b10_len = NumberUtils.base100toBase10(arr2,b100_start,sz,arr3,0);          String nStr = new String(arr3,0,b10_len);         if (!nStr.equals(istr)) {           System.out.println("ERROR! input="+istr+" output="+nStr);           System.out.println(arrstr(arr1,0,n));           System.out.println(arrstr(arr2,b100_start,sz));           System.out.println(arrstr(arr3,0,b10_len));         }        }     } else if ("b100sParse".equals(test)) {       int min=-1000000; int max=1000000;       String[] arr = new String[arrsz];       String[] internal = new String[arrsz];       for (int i=0; i<arrsz; i++) {         int val = rng.nextInt();         // todo - move to between min and max...         arr[i] = Integer.toString(rng.nextInt());       }       for (int i=0; i<iter; i++) {         int slot=i%arrsz;         internal[slot] = NumberUtils.base10toBase100SortableInt(arr[i%arrsz]);         ret += internal[slot].length();       }     } else if ("intParse".equals(test)) {       int min=-1000000; int max=1000000;       String[] arr = new String[arrsz];       String[] internal = new String[arrsz];       for (int i=0; i<arrsz; i++) {         int val = rng.nextInt();         // todo - move to between min and max...         arr[i] = Integer.toString(rng.nextInt());       }       for (int i=0; i<iter; i++) {         int slot=i%arrsz;         int val = Integer.parseInt(arr[i%arrsz]);         String sval = Integer.toString(val);         internal[slot] = sval;         ret += internal[slot].length();       }     } else if ("b100s".equals(test)) {       for (int i=0; i<iter; i++) {         Integer i1 = rng.nextInt();         Integer i2 = rng.nextInt();          // concentrate on small numbers for a while         // to try and hit boundary cases 0,1,-1,100,-100,etc         if (iter< 10000) {           i1 = (i1 % 250)-125;           i2 = (i2 % 250)-125;         } else if (iter< 500000) {           i1 = (i1 % 25000)-12500;           i2 = (i2 % 25000)-12500;         }          String s1=Integer.toString(i1);         String s2=Integer.toString(i2);         String v1 = NumberUtils.base10toBase10kSortableInt(s1);         String v2 = NumberUtils.base10toBase10kSortableInt(s2);         String out1=NumberUtils.base10kSortableIntToBase10(v1);         String out2=NumberUtils.base10kSortableIntToBase10(v2);          int c1 = i1.compareTo(i2);         int c2 = v1.compareTo(v2);         if (c1==0&& c2 !=0 || c1< 0&& c2>= 0 || c1> 0&& c2<=0             || !out1.equals(s1) || !out2.equals(s2))         {           System.out.println("Comparison error:"+s1+","+s2);           System.out.print("v1=");           for (int ii=0; ii<v1.length(); ii++) {             System.out.print(" " + (int)v1.charAt(ii));           }           System.out.print("\nv2=");           for (int ii=0; ii<v2.length(); ii++) {             System.out.print(" " + (int)v2.charAt(ii));           }           System.out.println("\nout1='"+out1+"', out2='" + out2 + "'");          }            }     }     ****/
name|long
name|endTime
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
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ret="
operator|+
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_interface
DECL|interface|Converter
interface|interface
name|Converter
block|{
DECL|method|toInternal
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
function_decl|;
DECL|method|toExternal
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
function_decl|;
block|}
end_interface
begin_class
DECL|class|Int2Int
class|class
name|Int2Int
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|SortInt
class|class
name|SortInt
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|int2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2int
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|SortLong
class|class
name|SortLong
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|long2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|Float2Float
class|class
name|Float2Float
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|SortFloat
class|class
name|SortFloat
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|float2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2floatStr
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|SortDouble
class|class
name|SortDouble
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|double2sortableStr
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|NumberUtils
operator|.
name|SortableStr2doubleStr
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|Base100S
class|class
name|Base100S
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base10toBase100SortableInt
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base100SortableIntToBase10
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|Base10kS
class|class
name|Base10kS
implements|implements
name|Converter
block|{
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base10toBase10kSortableInt
argument_list|(
name|val
argument_list|)
return|;
block|}
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|BCDUtils
operator|.
name|base10kSortableIntToBase10
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
end_unit
