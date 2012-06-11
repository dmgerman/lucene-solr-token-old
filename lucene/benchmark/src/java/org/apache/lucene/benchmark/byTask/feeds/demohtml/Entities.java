begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds.demohtml
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|demohtml
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * Utility class for encoding and decoding HTML entities.  */
end_comment
begin_class
DECL|class|Entities
specifier|public
class|class
name|Entities
block|{
DECL|field|decoder
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|decoder
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|300
argument_list|)
decl_stmt|;
DECL|field|encoder
specifier|static
specifier|final
name|String
index|[]
name|encoder
init|=
operator|new
name|String
index|[
literal|0x100
index|]
decl_stmt|;
DECL|method|decode
specifier|static
specifier|final
name|String
name|decode
parameter_list|(
name|String
name|entity
parameter_list|)
block|{
if|if
condition|(
name|entity
operator|.
name|charAt
argument_list|(
name|entity
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|';'
condition|)
comment|// remove trailing semicolon
name|entity
operator|=
name|entity
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|entity
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|entity
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
operator|==
literal|'#'
condition|)
block|{
name|int
name|start
init|=
literal|2
decl_stmt|;
name|int
name|radix
init|=
literal|10
decl_stmt|;
if|if
condition|(
name|entity
operator|.
name|charAt
argument_list|(
literal|2
argument_list|)
operator|==
literal|'X'
operator|||
name|entity
operator|.
name|charAt
argument_list|(
literal|2
argument_list|)
operator|==
literal|'x'
condition|)
block|{
name|start
operator|++
expr_stmt|;
name|radix
operator|=
literal|16
expr_stmt|;
block|}
name|Character
name|c
init|=
operator|new
name|Character
argument_list|(
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|entity
operator|.
name|substring
argument_list|(
name|start
argument_list|)
argument_list|,
name|radix
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|c
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
name|String
name|s
init|=
name|decoder
operator|.
name|get
argument_list|(
name|entity
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
return|return
name|s
return|;
else|else
return|return
literal|""
return|;
block|}
block|}
DECL|method|encode
specifier|public
specifier|static
specifier|final
name|String
name|encode
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|length
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|length
operator|*
literal|2
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|j
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|j
operator|<
literal|0x100
operator|&&
name|encoder
index|[
name|j
index|]
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|encoder
index|[
name|j
index|]
argument_list|)
expr_stmt|;
comment|// have a named encoding
name|buffer
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|j
operator|<
literal|0x80
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|j
argument_list|)
expr_stmt|;
comment|// use ASCII value
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"&#"
argument_list|)
expr_stmt|;
comment|// use numeric encoding
name|buffer
operator|.
name|append
argument_list|(
name|j
argument_list|)
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|add
specifier|static
specifier|final
name|void
name|add
parameter_list|(
name|String
name|entity
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|decoder
operator|.
name|put
argument_list|(
name|entity
argument_list|,
operator|(
operator|new
name|Character
argument_list|(
operator|(
name|char
operator|)
name|value
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|<
literal|0x100
condition|)
name|encoder
index|[
name|value
index|]
operator|=
name|entity
expr_stmt|;
block|}
static|static
block|{
name|add
argument_list|(
literal|"&nbsp"
argument_list|,
literal|160
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&iexcl"
argument_list|,
literal|161
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&cent"
argument_list|,
literal|162
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&pound"
argument_list|,
literal|163
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&curren"
argument_list|,
literal|164
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&yen"
argument_list|,
literal|165
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&brvbar"
argument_list|,
literal|166
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sect"
argument_list|,
literal|167
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&uml"
argument_list|,
literal|168
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&copy"
argument_list|,
literal|169
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ordf"
argument_list|,
literal|170
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&laquo"
argument_list|,
literal|171
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&not"
argument_list|,
literal|172
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&shy"
argument_list|,
literal|173
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&reg"
argument_list|,
literal|174
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&macr"
argument_list|,
literal|175
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&deg"
argument_list|,
literal|176
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&plusmn"
argument_list|,
literal|177
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sup2"
argument_list|,
literal|178
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sup3"
argument_list|,
literal|179
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&acute"
argument_list|,
literal|180
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&micro"
argument_list|,
literal|181
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&para"
argument_list|,
literal|182
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&middot"
argument_list|,
literal|183
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&cedil"
argument_list|,
literal|184
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sup1"
argument_list|,
literal|185
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ordm"
argument_list|,
literal|186
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&raquo"
argument_list|,
literal|187
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&frac14"
argument_list|,
literal|188
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&frac12"
argument_list|,
literal|189
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&frac34"
argument_list|,
literal|190
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&iquest"
argument_list|,
literal|191
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Agrave"
argument_list|,
literal|192
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Aacute"
argument_list|,
literal|193
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Acirc"
argument_list|,
literal|194
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Atilde"
argument_list|,
literal|195
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Auml"
argument_list|,
literal|196
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Aring"
argument_list|,
literal|197
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&AElig"
argument_list|,
literal|198
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ccedil"
argument_list|,
literal|199
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Egrave"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Eacute"
argument_list|,
literal|201
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ecirc"
argument_list|,
literal|202
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Euml"
argument_list|,
literal|203
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Igrave"
argument_list|,
literal|204
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Iacute"
argument_list|,
literal|205
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Icirc"
argument_list|,
literal|206
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Iuml"
argument_list|,
literal|207
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ETH"
argument_list|,
literal|208
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ntilde"
argument_list|,
literal|209
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ograve"
argument_list|,
literal|210
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Oacute"
argument_list|,
literal|211
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ocirc"
argument_list|,
literal|212
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Otilde"
argument_list|,
literal|213
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ouml"
argument_list|,
literal|214
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&times"
argument_list|,
literal|215
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Oslash"
argument_list|,
literal|216
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ugrave"
argument_list|,
literal|217
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Uacute"
argument_list|,
literal|218
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Ucirc"
argument_list|,
literal|219
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Uuml"
argument_list|,
literal|220
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Yacute"
argument_list|,
literal|221
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&THORN"
argument_list|,
literal|222
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&szlig"
argument_list|,
literal|223
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&agrave"
argument_list|,
literal|224
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&aacute"
argument_list|,
literal|225
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&acirc"
argument_list|,
literal|226
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&atilde"
argument_list|,
literal|227
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&auml"
argument_list|,
literal|228
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&aring"
argument_list|,
literal|229
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&aelig"
argument_list|,
literal|230
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ccedil"
argument_list|,
literal|231
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&egrave"
argument_list|,
literal|232
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&eacute"
argument_list|,
literal|233
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ecirc"
argument_list|,
literal|234
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&euml"
argument_list|,
literal|235
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&igrave"
argument_list|,
literal|236
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&iacute"
argument_list|,
literal|237
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&icirc"
argument_list|,
literal|238
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&iuml"
argument_list|,
literal|239
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&eth"
argument_list|,
literal|240
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ntilde"
argument_list|,
literal|241
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ograve"
argument_list|,
literal|242
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&oacute"
argument_list|,
literal|243
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ocirc"
argument_list|,
literal|244
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&otilde"
argument_list|,
literal|245
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ouml"
argument_list|,
literal|246
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&divide"
argument_list|,
literal|247
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&oslash"
argument_list|,
literal|248
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ugrave"
argument_list|,
literal|249
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&uacute"
argument_list|,
literal|250
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ucirc"
argument_list|,
literal|251
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&uuml"
argument_list|,
literal|252
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&yacute"
argument_list|,
literal|253
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&thorn"
argument_list|,
literal|254
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&yuml"
argument_list|,
literal|255
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&fnof"
argument_list|,
literal|402
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Alpha"
argument_list|,
literal|913
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Beta"
argument_list|,
literal|914
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Gamma"
argument_list|,
literal|915
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Delta"
argument_list|,
literal|916
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Epsilon"
argument_list|,
literal|917
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Zeta"
argument_list|,
literal|918
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Eta"
argument_list|,
literal|919
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Theta"
argument_list|,
literal|920
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Iota"
argument_list|,
literal|921
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Kappa"
argument_list|,
literal|922
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Lambda"
argument_list|,
literal|923
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Mu"
argument_list|,
literal|924
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Nu"
argument_list|,
literal|925
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Xi"
argument_list|,
literal|926
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Omicron"
argument_list|,
literal|927
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Pi"
argument_list|,
literal|928
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Rho"
argument_list|,
literal|929
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Sigma"
argument_list|,
literal|931
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Tau"
argument_list|,
literal|932
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Upsilon"
argument_list|,
literal|933
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Phi"
argument_list|,
literal|934
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Chi"
argument_list|,
literal|935
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Psi"
argument_list|,
literal|936
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Omega"
argument_list|,
literal|937
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&alpha"
argument_list|,
literal|945
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&beta"
argument_list|,
literal|946
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&gamma"
argument_list|,
literal|947
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&delta"
argument_list|,
literal|948
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&epsilon"
argument_list|,
literal|949
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&zeta"
argument_list|,
literal|950
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&eta"
argument_list|,
literal|951
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&theta"
argument_list|,
literal|952
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&iota"
argument_list|,
literal|953
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&kappa"
argument_list|,
literal|954
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lambda"
argument_list|,
literal|955
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&mu"
argument_list|,
literal|956
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&nu"
argument_list|,
literal|957
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&xi"
argument_list|,
literal|958
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&omicron"
argument_list|,
literal|959
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&pi"
argument_list|,
literal|960
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rho"
argument_list|,
literal|961
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sigmaf"
argument_list|,
literal|962
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sigma"
argument_list|,
literal|963
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&tau"
argument_list|,
literal|964
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&upsilon"
argument_list|,
literal|965
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&phi"
argument_list|,
literal|966
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&chi"
argument_list|,
literal|967
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&psi"
argument_list|,
literal|968
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&omega"
argument_list|,
literal|969
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&thetasym"
argument_list|,
literal|977
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&upsih"
argument_list|,
literal|978
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&piv"
argument_list|,
literal|982
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&bull"
argument_list|,
literal|8226
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&hellip"
argument_list|,
literal|8230
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&prime"
argument_list|,
literal|8242
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Prime"
argument_list|,
literal|8243
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&oline"
argument_list|,
literal|8254
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&frasl"
argument_list|,
literal|8260
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&weierp"
argument_list|,
literal|8472
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&image"
argument_list|,
literal|8465
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&real"
argument_list|,
literal|8476
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&trade"
argument_list|,
literal|8482
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&alefsym"
argument_list|,
literal|8501
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&larr"
argument_list|,
literal|8592
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&uarr"
argument_list|,
literal|8593
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rarr"
argument_list|,
literal|8594
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&darr"
argument_list|,
literal|8595
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&harr"
argument_list|,
literal|8596
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&crarr"
argument_list|,
literal|8629
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lArr"
argument_list|,
literal|8656
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&uArr"
argument_list|,
literal|8657
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rArr"
argument_list|,
literal|8658
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&dArr"
argument_list|,
literal|8659
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&hArr"
argument_list|,
literal|8660
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&forall"
argument_list|,
literal|8704
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&part"
argument_list|,
literal|8706
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&exist"
argument_list|,
literal|8707
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&empty"
argument_list|,
literal|8709
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&nabla"
argument_list|,
literal|8711
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&isin"
argument_list|,
literal|8712
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&notin"
argument_list|,
literal|8713
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ni"
argument_list|,
literal|8715
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&prod"
argument_list|,
literal|8719
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sum"
argument_list|,
literal|8721
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&minus"
argument_list|,
literal|8722
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lowast"
argument_list|,
literal|8727
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&radic"
argument_list|,
literal|8730
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&prop"
argument_list|,
literal|8733
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&infin"
argument_list|,
literal|8734
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ang"
argument_list|,
literal|8736
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&and"
argument_list|,
literal|8743
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&or"
argument_list|,
literal|8744
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&cap"
argument_list|,
literal|8745
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&cup"
argument_list|,
literal|8746
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&int"
argument_list|,
literal|8747
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&there4"
argument_list|,
literal|8756
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sim"
argument_list|,
literal|8764
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&cong"
argument_list|,
literal|8773
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&asymp"
argument_list|,
literal|8776
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ne"
argument_list|,
literal|8800
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&equiv"
argument_list|,
literal|8801
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&le"
argument_list|,
literal|8804
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ge"
argument_list|,
literal|8805
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sub"
argument_list|,
literal|8834
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sup"
argument_list|,
literal|8835
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&nsub"
argument_list|,
literal|8836
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sube"
argument_list|,
literal|8838
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&supe"
argument_list|,
literal|8839
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&oplus"
argument_list|,
literal|8853
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&otimes"
argument_list|,
literal|8855
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&perp"
argument_list|,
literal|8869
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sdot"
argument_list|,
literal|8901
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lceil"
argument_list|,
literal|8968
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rceil"
argument_list|,
literal|8969
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lfloor"
argument_list|,
literal|8970
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rfloor"
argument_list|,
literal|8971
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lang"
argument_list|,
literal|9001
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rang"
argument_list|,
literal|9002
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&loz"
argument_list|,
literal|9674
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&spades"
argument_list|,
literal|9824
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&clubs"
argument_list|,
literal|9827
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&hearts"
argument_list|,
literal|9829
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&diams"
argument_list|,
literal|9830
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&quot"
argument_list|,
literal|34
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&amp"
argument_list|,
literal|38
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lt"
argument_list|,
literal|60
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&gt"
argument_list|,
literal|62
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&OElig"
argument_list|,
literal|338
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&oelig"
argument_list|,
literal|339
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Scaron"
argument_list|,
literal|352
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&scaron"
argument_list|,
literal|353
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Yuml"
argument_list|,
literal|376
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&circ"
argument_list|,
literal|710
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&tilde"
argument_list|,
literal|732
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ensp"
argument_list|,
literal|8194
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&emsp"
argument_list|,
literal|8195
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&thinsp"
argument_list|,
literal|8201
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&zwnj"
argument_list|,
literal|8204
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&zwj"
argument_list|,
literal|8205
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lrm"
argument_list|,
literal|8206
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rlm"
argument_list|,
literal|8207
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ndash"
argument_list|,
literal|8211
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&mdash"
argument_list|,
literal|8212
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lsquo"
argument_list|,
literal|8216
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rsquo"
argument_list|,
literal|8217
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&sbquo"
argument_list|,
literal|8218
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&ldquo"
argument_list|,
literal|8220
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rdquo"
argument_list|,
literal|8221
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&bdquo"
argument_list|,
literal|8222
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&dagger"
argument_list|,
literal|8224
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&Dagger"
argument_list|,
literal|8225
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&permil"
argument_list|,
literal|8240
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&lsaquo"
argument_list|,
literal|8249
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&rsaquo"
argument_list|,
literal|8250
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"&euro"
argument_list|,
literal|8364
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
