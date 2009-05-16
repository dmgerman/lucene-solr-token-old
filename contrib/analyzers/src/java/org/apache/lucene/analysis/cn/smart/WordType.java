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
DECL|class|WordType
specifier|public
class|class
name|WordType
block|{
DECL|field|SENTENCE_BEGIN
specifier|public
specifier|final
specifier|static
name|int
name|SENTENCE_BEGIN
init|=
literal|0
decl_stmt|;
DECL|field|SENTENCE_END
specifier|public
specifier|final
specifier|static
name|int
name|SENTENCE_END
init|=
literal|1
decl_stmt|;
comment|// å¥å­çå¼å¤´åç»æ
DECL|field|CHINESE_WORD
specifier|public
specifier|final
specifier|static
name|int
name|CHINESE_WORD
init|=
literal|2
decl_stmt|;
comment|// ä¸­æè¯
DECL|field|STRING
specifier|public
specifier|final
specifier|static
name|int
name|STRING
init|=
literal|3
decl_stmt|;
DECL|field|NUMBER
specifier|public
specifier|final
specifier|static
name|int
name|NUMBER
init|=
literal|4
decl_stmt|;
comment|// asciiå­ç¬¦ä¸²åæ°å­
DECL|field|DELIMITER
specifier|public
specifier|final
specifier|static
name|int
name|DELIMITER
init|=
literal|5
decl_stmt|;
comment|// æææ ç¹ç¬¦å·
DECL|field|FULLWIDTH_STRING
specifier|public
specifier|final
specifier|static
name|int
name|FULLWIDTH_STRING
init|=
literal|6
decl_stmt|;
DECL|field|FULLWIDTH_NUMBER
specifier|public
specifier|final
specifier|static
name|int
name|FULLWIDTH_NUMBER
init|=
literal|7
decl_stmt|;
comment|// å«æå¨è§å­ç¬¦çå­ç¬¦ä¸²ï¼å«å¨è§æ°å­çæ°å­
block|}
end_class
end_unit
