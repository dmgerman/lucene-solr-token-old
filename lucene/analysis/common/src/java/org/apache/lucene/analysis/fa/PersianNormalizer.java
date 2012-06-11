begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fa
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fa
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|StemmerUtil
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Normalizer for Persian.  *<p>  * Normalization is done in-place for efficiency, operating on a termbuffer.  *<p>  * Normalization is defined as:  *<ul>  *<li>Normalization of various heh + hamza forms and heh goal to heh.  *<li>Normalization of farsi yeh and yeh barree to arabic yeh  *<li>Normalization of persian keheh to arabic kaf  *</ul>  *   */
end_comment
begin_class
DECL|class|PersianNormalizer
specifier|public
class|class
name|PersianNormalizer
block|{
DECL|field|YEH
specifier|public
specifier|static
specifier|final
name|char
name|YEH
init|=
literal|'\u064A'
decl_stmt|;
DECL|field|FARSI_YEH
specifier|public
specifier|static
specifier|final
name|char
name|FARSI_YEH
init|=
literal|'\u06CC'
decl_stmt|;
DECL|field|YEH_BARREE
specifier|public
specifier|static
specifier|final
name|char
name|YEH_BARREE
init|=
literal|'\u06D2'
decl_stmt|;
DECL|field|KEHEH
specifier|public
specifier|static
specifier|final
name|char
name|KEHEH
init|=
literal|'\u06A9'
decl_stmt|;
DECL|field|KAF
specifier|public
specifier|static
specifier|final
name|char
name|KAF
init|=
literal|'\u0643'
decl_stmt|;
DECL|field|HAMZA_ABOVE
specifier|public
specifier|static
specifier|final
name|char
name|HAMZA_ABOVE
init|=
literal|'\u0654'
decl_stmt|;
DECL|field|HEH_YEH
specifier|public
specifier|static
specifier|final
name|char
name|HEH_YEH
init|=
literal|'\u06C0'
decl_stmt|;
DECL|field|HEH_GOAL
specifier|public
specifier|static
specifier|final
name|char
name|HEH_GOAL
init|=
literal|'\u06C1'
decl_stmt|;
DECL|field|HEH
specifier|public
specifier|static
specifier|final
name|char
name|HEH
init|=
literal|'\u0647'
decl_stmt|;
comment|/**    * Normalize an input buffer of Persian text    *     * @param s input buffer    * @param len length of input buffer    * @return length of input buffer after normalization    */
DECL|method|normalize
specifier|public
name|int
name|normalize
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|s
index|[
name|i
index|]
condition|)
block|{
case|case
name|FARSI_YEH
case|:
case|case
name|YEH_BARREE
case|:
name|s
index|[
name|i
index|]
operator|=
name|YEH
expr_stmt|;
break|break;
case|case
name|KEHEH
case|:
name|s
index|[
name|i
index|]
operator|=
name|KAF
expr_stmt|;
break|break;
case|case
name|HEH_YEH
case|:
case|case
name|HEH_GOAL
case|:
name|s
index|[
name|i
index|]
operator|=
name|HEH
expr_stmt|;
break|break;
case|case
name|HAMZA_ABOVE
case|:
comment|// necessary for HEH + HAMZA
name|len
operator|=
name|delete
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
return|return
name|len
return|;
block|}
block|}
end_class
end_unit
