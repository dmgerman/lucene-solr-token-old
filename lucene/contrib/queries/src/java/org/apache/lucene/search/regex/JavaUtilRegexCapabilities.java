begin_unit
begin_package
DECL|package|org.apache.lucene.search.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|regex
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
import|;
end_import
begin_comment
comment|/**  * An implementation tying Java's built-in java.util.regex to RegexQuery.  *  * Note that because this implementation currently only returns null from  * {@link RegexCapabilities.RegexMatcher#prefix()} that queries using this implementation   * will enumerate and attempt to {@link RegexCapabilities.RegexMatcher#match(BytesRef)} each   * term for the specified field in the index.  */
end_comment
begin_class
DECL|class|JavaUtilRegexCapabilities
specifier|public
class|class
name|JavaUtilRegexCapabilities
implements|implements
name|RegexCapabilities
block|{
DECL|field|flags
specifier|private
name|int
name|flags
init|=
literal|0
decl_stmt|;
comment|// Define the optional flags from Pattern that can be used.
comment|// Do this here to keep Pattern contained within this class.
DECL|field|FLAG_CANON_EQ
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_CANON_EQ
init|=
name|Pattern
operator|.
name|CANON_EQ
decl_stmt|;
DECL|field|FLAG_CASE_INSENSITIVE
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_CASE_INSENSITIVE
init|=
name|Pattern
operator|.
name|CASE_INSENSITIVE
decl_stmt|;
DECL|field|FLAG_COMMENTS
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_COMMENTS
init|=
name|Pattern
operator|.
name|COMMENTS
decl_stmt|;
DECL|field|FLAG_DOTALL
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_DOTALL
init|=
name|Pattern
operator|.
name|DOTALL
decl_stmt|;
DECL|field|FLAG_LITERAL
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_LITERAL
init|=
name|Pattern
operator|.
name|LITERAL
decl_stmt|;
DECL|field|FLAG_MULTILINE
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_MULTILINE
init|=
name|Pattern
operator|.
name|MULTILINE
decl_stmt|;
DECL|field|FLAG_UNICODE_CASE
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_UNICODE_CASE
init|=
name|Pattern
operator|.
name|UNICODE_CASE
decl_stmt|;
DECL|field|FLAG_UNIX_LINES
specifier|public
specifier|static
specifier|final
name|int
name|FLAG_UNIX_LINES
init|=
name|Pattern
operator|.
name|UNIX_LINES
decl_stmt|;
comment|/**    * Default constructor that uses java.util.regex.Pattern     * with its default flags.    */
DECL|method|JavaUtilRegexCapabilities
specifier|public
name|JavaUtilRegexCapabilities
parameter_list|()
block|{
name|this
operator|.
name|flags
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Constructor that allows for the modification of the flags that    * the java.util.regex.Pattern will use to compile the regular expression.    * This gives the user the ability to fine-tune how the regular expression     * to match the functionality that they need.     * The {@link java.util.regex.Pattern Pattern} class supports specifying     * these fields via the regular expression text itself, but this gives the caller    * another option to modify the behavior. Useful in cases where the regular expression text    * cannot be modified, or if doing so is undesired.    *     * @param flags The flags that are ORed together.    */
DECL|method|JavaUtilRegexCapabilities
specifier|public
name|JavaUtilRegexCapabilities
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|compile
specifier|public
name|RegexCapabilities
operator|.
name|RegexMatcher
name|compile
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
operator|new
name|JavaUtilRegexMatcher
argument_list|(
name|regex
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|flags
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|JavaUtilRegexCapabilities
name|other
init|=
operator|(
name|JavaUtilRegexCapabilities
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|flags
operator|!=
name|other
operator|.
name|flags
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|class|JavaUtilRegexMatcher
class|class
name|JavaUtilRegexMatcher
implements|implements
name|RegexCapabilities
operator|.
name|RegexMatcher
block|{
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
DECL|field|utf16
specifier|private
specifier|final
name|CharsRef
name|utf16
init|=
operator|new
name|CharsRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|method|JavaUtilRegexMatcher
specifier|public
name|JavaUtilRegexMatcher
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|this
operator|.
name|pattern
operator|.
name|matcher
argument_list|(
name|utf16
argument_list|)
expr_stmt|;
block|}
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
argument_list|,
name|term
operator|.
name|length
argument_list|,
name|utf16
argument_list|)
expr_stmt|;
return|return
name|matcher
operator|.
name|reset
argument_list|()
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|prefix
specifier|public
name|String
name|prefix
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
