begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
operator|.
name|SegTokenFilter
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
begin_comment
comment|/**  * SmartChineseAnalyzer utility constants and methods  *<p><font color="#FF0000">  * WARNING: The status of the analyzers/smartcn<b>analysis.cn</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *</p>  */
end_comment
begin_class
DECL|class|Utility
specifier|public
class|class
name|Utility
block|{
DECL|field|STRING_CHAR_ARRAY
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|STRING_CHAR_ARRAY
init|=
operator|new
name|String
argument_list|(
literal|"æª##ä¸²"
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|NUMBER_CHAR_ARRAY
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|NUMBER_CHAR_ARRAY
init|=
operator|new
name|String
argument_list|(
literal|"æª##æ°"
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|START_CHAR_ARRAY
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|START_CHAR_ARRAY
init|=
operator|new
name|String
argument_list|(
literal|"å§##å§"
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|field|END_CHAR_ARRAY
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|END_CHAR_ARRAY
init|=
operator|new
name|String
argument_list|(
literal|"æ«##æ«"
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|/**    * Delimiters will be filtered to this character by {@link SegTokenFilter}    */
DECL|field|COMMON_DELIMITER
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|COMMON_DELIMITER
init|=
operator|new
name|char
index|[]
block|{
literal|','
block|}
decl_stmt|;
comment|/**    * Space-like characters that need to be skipped: such as space, tab, newline, carriage return.    */
DECL|field|SPACES
specifier|public
specifier|static
specifier|final
name|String
name|SPACES
init|=
literal|" ã\t\r\n"
decl_stmt|;
comment|/**    * Maximum bigram frequency (used in the smoothing function).     */
DECL|field|MAX_FREQUENCE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_FREQUENCE
init|=
literal|2079997
operator|+
literal|80000
decl_stmt|;
comment|/**    * compare two arrays starting at the specified offsets.    *     * @param larray left array    * @param lstartIndex start offset into larray    * @param rarray right array    * @param rstartIndex start offset into rarray    * @return 0 if the arrays are equalï¼1 if larray> rarray, -1 if larray< rarray    */
DECL|method|compareArray
specifier|public
specifier|static
name|int
name|compareArray
parameter_list|(
name|char
index|[]
name|larray
parameter_list|,
name|int
name|lstartIndex
parameter_list|,
name|char
index|[]
name|rarray
parameter_list|,
name|int
name|rstartIndex
parameter_list|)
block|{
if|if
condition|(
name|larray
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|rarray
operator|==
literal|null
operator|||
name|rstartIndex
operator|>=
name|rarray
operator|.
name|length
condition|)
return|return
literal|0
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
comment|// larray != null
if|if
condition|(
name|rarray
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|lstartIndex
operator|>=
name|larray
operator|.
name|length
condition|)
return|return
literal|0
return|;
else|else
return|return
literal|1
return|;
block|}
block|}
name|int
name|li
init|=
name|lstartIndex
decl_stmt|,
name|ri
init|=
name|rstartIndex
decl_stmt|;
while|while
condition|(
name|li
operator|<
name|larray
operator|.
name|length
operator|&&
name|ri
operator|<
name|rarray
operator|.
name|length
operator|&&
name|larray
index|[
name|li
index|]
operator|==
name|rarray
index|[
name|ri
index|]
condition|)
block|{
name|li
operator|++
expr_stmt|;
name|ri
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|li
operator|==
name|larray
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|ri
operator|==
name|rarray
operator|.
name|length
condition|)
block|{
comment|// Both arrays are equivalent, return 0.
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// larray< rarray because larray has ended first.
return|return
operator|-
literal|1
return|;
block|}
block|}
else|else
block|{
comment|// differing lengths
if|if
condition|(
name|ri
operator|==
name|rarray
operator|.
name|length
condition|)
block|{
comment|// larray> rarray because rarray has ended first.
return|return
literal|1
return|;
block|}
else|else
block|{
comment|// determine by comparison
if|if
condition|(
name|larray
index|[
name|li
index|]
operator|>
name|rarray
index|[
name|ri
index|]
condition|)
return|return
literal|1
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
comment|/**    * Compare two arrays, starting at the specified offsets, but treating shortArray as a prefix to longArray.    * As long as shortArray is a prefix of longArray, return 0.    * Otherwise, behave as {@link Utility#compareArray(char[], int, char[], int)}    *     * @param shortArray prefix array    * @param shortIndex offset into shortArray    * @param longArray long array (word)    * @param longIndex offset into longArray    * @return 0 if shortArray is a prefix of longArray, otherwise act as {@link Utility#compareArray(char[], int, char[], int)}    */
DECL|method|compareArrayByPrefix
specifier|public
specifier|static
name|int
name|compareArrayByPrefix
parameter_list|(
name|char
index|[]
name|shortArray
parameter_list|,
name|int
name|shortIndex
parameter_list|,
name|char
index|[]
name|longArray
parameter_list|,
name|int
name|longIndex
parameter_list|)
block|{
comment|// a null prefix is a prefix of longArray
if|if
condition|(
name|shortArray
operator|==
literal|null
condition|)
return|return
literal|0
return|;
elseif|else
if|if
condition|(
name|longArray
operator|==
literal|null
condition|)
return|return
operator|(
name|shortIndex
operator|<
name|shortArray
operator|.
name|length
operator|)
condition|?
literal|1
else|:
literal|0
return|;
name|int
name|si
init|=
name|shortIndex
decl_stmt|,
name|li
init|=
name|longIndex
decl_stmt|;
while|while
condition|(
name|si
operator|<
name|shortArray
operator|.
name|length
operator|&&
name|li
operator|<
name|longArray
operator|.
name|length
operator|&&
name|shortArray
index|[
name|si
index|]
operator|==
name|longArray
index|[
name|li
index|]
condition|)
block|{
name|si
operator|++
expr_stmt|;
name|li
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|si
operator|==
name|shortArray
operator|.
name|length
condition|)
block|{
comment|// shortArray is a prefix of longArray
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// shortArray> longArray because longArray ended first.
if|if
condition|(
name|li
operator|==
name|longArray
operator|.
name|length
condition|)
return|return
literal|1
return|;
else|else
comment|// determine by comparison
return|return
operator|(
name|shortArray
index|[
name|si
index|]
operator|>
name|longArray
index|[
name|li
index|]
operator|)
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
comment|/**    * Return the internal {@link CharType} constant of a given character.     * @param ch input character    * @return constant from {@link CharType} describing the character type.    *     * @see CharType    */
DECL|method|getCharType
specifier|public
specifier|static
name|int
name|getCharType
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
comment|// Most (but not all!) of these are Han Ideographic Characters
if|if
condition|(
name|ch
operator|>=
literal|0x4E00
operator|&&
name|ch
operator|<=
literal|0x9FA5
condition|)
return|return
name|CharType
operator|.
name|HANZI
return|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|0x0041
operator|&&
name|ch
operator|<=
literal|0x005A
operator|)
operator|||
operator|(
name|ch
operator|>=
literal|0x0061
operator|&&
name|ch
operator|<=
literal|0x007A
operator|)
condition|)
return|return
name|CharType
operator|.
name|LETTER
return|;
if|if
condition|(
name|ch
operator|>=
literal|0x0030
operator|&&
name|ch
operator|<=
literal|0x0039
condition|)
return|return
name|CharType
operator|.
name|DIGIT
return|;
if|if
condition|(
name|ch
operator|==
literal|' '
operator|||
name|ch
operator|==
literal|'\t'
operator|||
name|ch
operator|==
literal|'\r'
operator|||
name|ch
operator|==
literal|'\n'
operator|||
name|ch
operator|==
literal|'ã'
condition|)
return|return
name|CharType
operator|.
name|SPACE_LIKE
return|;
comment|// Punctuation Marks
if|if
condition|(
operator|(
name|ch
operator|>=
literal|0x0021
operator|&&
name|ch
operator|<=
literal|0x00BB
operator|)
operator|||
operator|(
name|ch
operator|>=
literal|0x2010
operator|&&
name|ch
operator|<=
literal|0x2642
operator|)
operator|||
operator|(
name|ch
operator|>=
literal|0x3001
operator|&&
name|ch
operator|<=
literal|0x301E
operator|)
condition|)
return|return
name|CharType
operator|.
name|DELIMITER
return|;
comment|// Full-Width range
if|if
condition|(
operator|(
name|ch
operator|>=
literal|0xFF21
operator|&&
name|ch
operator|<=
literal|0xFF3A
operator|)
operator|||
operator|(
name|ch
operator|>=
literal|0xFF41
operator|&&
name|ch
operator|<=
literal|0xFF5A
operator|)
condition|)
return|return
name|CharType
operator|.
name|FULLWIDTH_LETTER
return|;
if|if
condition|(
name|ch
operator|>=
literal|0xFF10
operator|&&
name|ch
operator|<=
literal|0xFF19
condition|)
return|return
name|CharType
operator|.
name|FULLWIDTH_DIGIT
return|;
if|if
condition|(
name|ch
operator|>=
literal|0xFE30
operator|&&
name|ch
operator|<=
literal|0xFF63
condition|)
return|return
name|CharType
operator|.
name|DELIMITER
return|;
return|return
name|CharType
operator|.
name|OTHER
return|;
block|}
block|}
end_class
end_unit
