begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_interface
DECL|interface|HighlightParams
specifier|public
interface|interface
name|HighlightParams
block|{
DECL|field|HIGHLIGHT
specifier|public
specifier|static
specifier|final
name|String
name|HIGHLIGHT
init|=
literal|"hl"
decl_stmt|;
DECL|field|FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS
init|=
name|HIGHLIGHT
operator|+
literal|".fl"
decl_stmt|;
DECL|field|SNIPPETS
specifier|public
specifier|static
specifier|final
name|String
name|SNIPPETS
init|=
name|HIGHLIGHT
operator|+
literal|".snippets"
decl_stmt|;
DECL|field|FRAGSIZE
specifier|public
specifier|static
specifier|final
name|String
name|FRAGSIZE
init|=
name|HIGHLIGHT
operator|+
literal|".fragsize"
decl_stmt|;
DECL|field|INCREMENT
specifier|public
specifier|static
specifier|final
name|String
name|INCREMENT
init|=
name|HIGHLIGHT
operator|+
literal|".increment"
decl_stmt|;
DECL|field|MAX_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_CHARS
init|=
name|HIGHLIGHT
operator|+
literal|".maxAnalyzedChars"
decl_stmt|;
DECL|field|FORMATTER
specifier|public
specifier|static
specifier|final
name|String
name|FORMATTER
init|=
name|HIGHLIGHT
operator|+
literal|".formatter"
decl_stmt|;
DECL|field|FRAGMENTER
specifier|public
specifier|static
specifier|final
name|String
name|FRAGMENTER
init|=
name|HIGHLIGHT
operator|+
literal|".fragmenter"
decl_stmt|;
DECL|field|FIELD_MATCH
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_MATCH
init|=
name|HIGHLIGHT
operator|+
literal|".requireFieldMatch"
decl_stmt|;
comment|// Formatter
DECL|field|SIMPLE
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE
init|=
literal|"simple"
decl_stmt|;
DECL|field|SIMPLE_PRE
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE_PRE
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SIMPLE
operator|+
literal|".pre"
decl_stmt|;
DECL|field|SIMPLE_POST
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE_POST
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|SIMPLE
operator|+
literal|".post"
decl_stmt|;
comment|// Regex fragmenter
DECL|field|REGEX
specifier|public
specifier|static
specifier|final
name|String
name|REGEX
init|=
literal|"regex"
decl_stmt|;
DECL|field|SLOP
specifier|public
specifier|static
specifier|final
name|String
name|SLOP
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|REGEX
operator|+
literal|".slop"
decl_stmt|;
DECL|field|PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|REGEX
operator|+
literal|".pattern"
decl_stmt|;
DECL|field|MAX_RE_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|MAX_RE_CHARS
init|=
name|HIGHLIGHT
operator|+
literal|"."
operator|+
name|REGEX
operator|+
literal|".maxAnalyzedChars"
decl_stmt|;
block|}
end_interface
end_unit
