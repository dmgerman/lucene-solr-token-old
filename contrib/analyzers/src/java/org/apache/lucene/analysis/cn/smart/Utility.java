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
comment|/**    * éè¦è·³è¿çç¬¦å·ï¼ä¾å¦å¶è¡¨ç¬¦ï¼åè½¦ï¼æ¢è¡ç­ç­ã    */
DECL|field|SPACES
specifier|public
specifier|static
specifier|final
name|String
name|SPACES
init|=
literal|" ã\t\r\n"
decl_stmt|;
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
comment|/**    * æ¯è¾ä¸¤ä¸ªæ´æ°æ°ç»çå¤§å°, åå«ä»æ°ç»çä¸å®ä½ç½®å¼å§éä¸ªæ¯è¾, å½ä¾æ¬¡ç¸ç­ä¸é½å°è¾¾æ«å°¾æ¶, è¿åç¸ç­, å¦åæªå°è¾¾æ«å°¾çå¤§äºå°è¾¾æ«å°¾ç;    * å½æªå°è¾¾æ«å°¾æ¶æä¸ä½ä¸ç¸ç­, è¯¥ä½ç½®æ°å¼å¤§çæ°ç»å¤§äºå°ç    *     * @param larray    * @param lstartIndex larrayçèµ·å§ä½ç½®    * @param rarray    * @param rstartIndex rarrayçèµ·å§ä½ç½®    * @return 0è¡¨ç¤ºç¸ç­ï¼1è¡¨ç¤ºlarray> rarray, -1è¡¨ç¤ºlarray< rarray    */
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
comment|// ä¸¤èä¸ç´ç¸ç­å°æ«å°¾ï¼å æ­¤è¿åç¸ç­ï¼ä¹å°±æ¯ç»æ0
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// æ­¤æ¶ä¸å¯è½ri>rarray.lengthå æ­¤åªæri<rarray.length
comment|// è¡¨ç¤ºlarrayå·²ç»ç»æï¼rarrayæ²¡æç»æï¼å æ­¤larray< rarrayï¼è¿å-1
return|return
operator|-
literal|1
return|;
block|}
block|}
else|else
block|{
comment|// æ­¤æ¶ä¸å¯è½li>larray.lengthå æ­¤åªæli< larray.lengthï¼è¡¨ç¤ºliæ²¡æå°è¾¾larrayæ«å°¾
if|if
condition|(
name|ri
operator|==
name|rarray
operator|.
name|length
condition|)
block|{
comment|// larrayæ²¡æç»æï¼ä½æ¯rarrayå·²ç»ç»æï¼å æ­¤larray> rarray
return|return
literal|1
return|;
block|}
else|else
block|{
comment|// æ­¤æ¶ä¸å¯è½ri>rarray.lengthå æ­¤åªæri< rarray.length
comment|// è¡¨ç¤ºlarrayårarrayé½æ²¡æç»æï¼å æ­¤æä¸ä¸ä¸ªæ°çå¤§å°å¤æ­
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
comment|/**    * æ ¹æ®åç¼æ¥å¤æ­ä¸¤ä¸ªå­ç¬¦æ°ç»çå¤§å°ï¼å½åèä¸ºåèçåç¼æ¶ï¼è¡¨ç¤ºç¸ç­ï¼å½ä¸ä¸ºåç¼æ¶ï¼æç§æ®éå­ç¬¦ä¸²æ¹å¼æ¯è¾    *     * @param shortArray    * @param shortIndex    * @param longArray    * @param longIndex    * @return    */
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
comment|// ç©ºæ°ç»æ¯æææ°ç»çåç¼ï¼ä¸èèindex
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
comment|// shortArray æ¯ longArrayçprefix
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// æ­¤æ¶ä¸å¯è½si>shortArray.lengthå æ­¤åªæsi<
comment|// shortArray.lengthï¼è¡¨ç¤ºsiæ²¡æå°è¾¾shortArrayæ«å°¾
comment|// shortArrayæ²¡æç»æï¼ä½æ¯longArrayå·²ç»ç»æï¼å æ­¤shortArray> longArray
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
comment|// æ­¤æ¶ä¸å¯è½li>longArray.lengthå æ­¤åªæli< longArray.length
comment|// è¡¨ç¤ºshortArrayålongArrayé½æ²¡æç»æï¼å æ­¤æä¸ä¸ä¸ªæ°çå¤§å°å¤æ­
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
comment|// æå¤çæ¯æ±å­
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
comment|// æåé¢çå¶å®çé½æ¯æ ç¹ç¬¦å·äº
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
comment|// å¨è§å­ç¬¦åºå
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
