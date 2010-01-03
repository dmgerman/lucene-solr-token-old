begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
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
name|AbstractSet
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharacterUtils
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A simple class that stores Strings as char[]'s in a  * hash table.  Note that this is not a general purpose  * class.  For example, it cannot remove items from the  * set, nor does it resize its hash table to be smaller,  * etc.  It is designed to be quick to test if a char[]  * is in the set without the necessity of converting it  * to a String first.  *<p>You must specify the required {@link Version}  * compatibility when creating {@link CharArraySet}:  *<ul>  *<li> As of 3.1, supplementary characters are  *       properly lowercased.</li>  *</ul>  * Before 3.1 supplementary characters could not be  * lowercased correctly due to the lack of Unicode 4  * support in JDK 1.4. To use instances of  * {@link CharArraySet} with the behavior before Lucene  * 3.1 pass a {@link Version}< 3.1 to the constructors.  *<P>  *<em>Please note:</em> This class implements {@link java.util.Set Set} but  * does not behave like it should in all cases. The generic type is  * {@code Set<Object>}, because you can add any object to it,  * that has a string representation. The add methods will use  * {@link Object#toString} and store the result using a {@code char[]}  * buffer. The same behavior have the {@code contains()} methods.  * The {@link #iterator()} returns an {@code Iterator<String>}.  * For type safety also {@link #stringIterator()} is provided.  */
end_comment
begin_class
DECL|class|CharArraySet
specifier|public
class|class
name|CharArraySet
extends|extends
name|AbstractSet
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|INIT_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|INIT_SIZE
init|=
literal|8
decl_stmt|;
DECL|field|entries
specifier|private
name|char
index|[]
index|[]
name|entries
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|EMPTY_SET
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|EMPTY_SET
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|charUtils
specifier|private
specifier|final
name|CharacterUtils
name|charUtils
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Create set with enough capacity to hold startSize terms    *     * @param matchVersion    *          compatibility match version see<a href="#version">Version    *          note</a> above for details.    * @param startSize    *          the initial capacity    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    */
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|int
name|startSize
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|int
name|size
init|=
name|INIT_SIZE
decl_stmt|;
while|while
condition|(
name|startSize
operator|+
operator|(
name|startSize
operator|>>
literal|2
operator|)
operator|>
name|size
condition|)
name|size
operator|<<=
literal|1
expr_stmt|;
name|entries
operator|=
operator|new
name|char
index|[
name|size
index|]
index|[]
expr_stmt|;
name|this
operator|.
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Creates a set from a Collection of objects.     *     * @param matchVersion    *          compatibility match version see<a href="#version">Version    *          note</a> above for details.    * @param c    *          a collection whose elements to be placed into the set    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    */
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|c
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|c
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a set with enough capacity to hold startSize terms    *     * @param startSize    *          the initial capacity    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    * @deprecated use {@link #CharArraySet(Version, int, boolean)} instead    */
annotation|@
name|Deprecated
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|int
name|startSize
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|startSize
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a set from a Collection of objects.     *     * @param c    *          a collection whose elements to be placed into the set    * @param ignoreCase    *<code>false</code> if and only if the set should be case sensitive    *          otherwise<code>true</code>.    * @deprecated use {@link #CharArraySet(Version, Collection, boolean)} instead             */
annotation|@
name|Deprecated
DECL|method|CharArraySet
specifier|public
name|CharArraySet
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|c
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
block|{
name|this
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|c
operator|.
name|size
argument_list|()
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|/** Create set from entries */
DECL|method|CharArraySet
specifier|private
name|CharArraySet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|char
index|[]
index|[]
name|entries
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|=
name|entries
expr_stmt|;
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/** Clears all entries in this set. This method is supported for reusing, but not {@link Set#remove}. */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|entries
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** true if the<code>len</code> chars of<code>text</code> starting at<code>off</code>    * are in the set */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
return|return
name|entries
index|[
name|getSlot
argument_list|(
name|text
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
index|]
operator|!=
literal|null
return|;
block|}
comment|/** true if the<code>CharSequence</code> is in the set */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|CharSequence
name|cs
parameter_list|)
block|{
return|return
name|entries
index|[
name|getSlot
argument_list|(
name|cs
argument_list|)
index|]
operator|!=
literal|null
return|;
block|}
DECL|method|getSlot
specifier|private
name|int
name|getSlot
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|code
init|=
name|getHashCode
argument_list|(
name|text
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|code
operator|&
operator|(
name|entries
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
name|char
index|[]
name|text2
init|=
name|entries
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|text2
operator|!=
literal|null
operator|&&
operator|!
name|equals
argument_list|(
name|text
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|text2
argument_list|)
condition|)
block|{
specifier|final
name|int
name|inc
init|=
operator|(
operator|(
name|code
operator|>>
literal|8
operator|)
operator|+
name|code
operator|)
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|pos
operator|=
name|code
operator|&
operator|(
name|entries
operator|.
name|length
operator|-
literal|1
operator|)
expr_stmt|;
name|text2
operator|=
name|entries
index|[
name|pos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|text2
operator|!=
literal|null
operator|&&
operator|!
name|equals
argument_list|(
name|text
argument_list|,
name|off
argument_list|,
name|len
argument_list|,
name|text2
argument_list|)
condition|)
do|;
block|}
return|return
name|pos
return|;
block|}
comment|/** Returns true if the String is in the set */
DECL|method|getSlot
specifier|private
name|int
name|getSlot
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|int
name|code
init|=
name|getHashCode
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
name|code
operator|&
operator|(
name|entries
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
name|char
index|[]
name|text2
init|=
name|entries
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
name|text2
operator|!=
literal|null
operator|&&
operator|!
name|equals
argument_list|(
name|text
argument_list|,
name|text2
argument_list|)
condition|)
block|{
specifier|final
name|int
name|inc
init|=
operator|(
operator|(
name|code
operator|>>
literal|8
operator|)
operator|+
name|code
operator|)
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|pos
operator|=
name|code
operator|&
operator|(
name|entries
operator|.
name|length
operator|-
literal|1
operator|)
expr_stmt|;
name|text2
operator|=
name|entries
index|[
name|pos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|text2
operator|!=
literal|null
operator|&&
operator|!
name|equals
argument_list|(
name|text
argument_list|,
name|text2
argument_list|)
condition|)
do|;
block|}
return|return
name|pos
return|;
block|}
comment|/** Add this CharSequence into the set */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
return|;
comment|// could be more efficient
block|}
comment|/** Add this String into the set */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|text
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
block|}
comment|/** Add this char[] directly to the set.    * If ignoreCase is true for this Set, the text array will be directly modified.    * The user should never modify this text array after calling this method.    */
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|char
index|[]
name|text
parameter_list|)
block|{
if|if
condition|(
name|ignoreCase
condition|)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
condition|;
control|)
block|{
name|i
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|,
name|text
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|slot
init|=
name|getSlot
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|entries
index|[
name|slot
index|]
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
name|entries
index|[
name|slot
index|]
operator|=
name|text
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|+
operator|(
name|count
operator|>>
literal|2
operator|)
operator|>
name|entries
operator|.
name|length
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|equals
specifier|private
name|boolean
name|equals
parameter_list|(
name|char
index|[]
name|text1
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|char
index|[]
name|text2
parameter_list|)
block|{
if|if
condition|(
name|len
operator|!=
name|text2
operator|.
name|length
condition|)
return|return
literal|false
return|;
specifier|final
name|int
name|limit
init|=
name|off
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|ignoreCase
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
name|len
condition|;
control|)
block|{
specifier|final
name|int
name|codePointAt
init|=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text1
argument_list|,
name|off
operator|+
name|i
argument_list|,
name|limit
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codePointAt
argument_list|)
operator|!=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text2
argument_list|,
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePointAt
argument_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|text1
index|[
name|off
operator|+
name|i
index|]
operator|!=
name|text2
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|equals
specifier|private
name|boolean
name|equals
parameter_list|(
name|CharSequence
name|text1
parameter_list|,
name|char
index|[]
name|text2
parameter_list|)
block|{
name|int
name|len
init|=
name|text1
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|text2
operator|.
name|length
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|ignoreCase
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
name|len
condition|;
control|)
block|{
specifier|final
name|int
name|codePointAt
init|=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text1
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codePointAt
argument_list|)
operator|!=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text2
argument_list|,
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePointAt
argument_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|text1
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|!=
name|text2
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
block|{
specifier|final
name|int
name|newSize
init|=
literal|2
operator|*
name|entries
operator|.
name|length
decl_stmt|;
name|char
index|[]
index|[]
name|oldEntries
init|=
name|entries
decl_stmt|;
name|entries
operator|=
operator|new
name|char
index|[
name|newSize
index|]
index|[]
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
name|oldEntries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
index|[]
name|text
init|=
name|oldEntries
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
comment|// todo: could be faster... no need to compare strings on collision
name|entries
index|[
name|getSlot
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
index|]
operator|=
name|text
expr_stmt|;
block|}
block|}
block|}
DECL|method|getHashCode
specifier|private
name|int
name|getHashCode
parameter_list|(
name|char
index|[]
name|text
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|stop
init|=
name|offset
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|ignoreCase
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|stop
condition|;
control|)
block|{
specifier|final
name|int
name|codePointAt
init|=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text
argument_list|,
name|i
argument_list|,
name|stop
argument_list|)
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codePointAt
argument_list|)
expr_stmt|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePointAt
argument_list|)
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
name|offset
init|;
name|i
operator|<
name|stop
condition|;
name|i
operator|++
control|)
block|{
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|text
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|code
return|;
block|}
DECL|method|getHashCode
specifier|private
name|int
name|getHashCode
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|ignoreCase
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
name|len
condition|;
control|)
block|{
name|int
name|codePointAt
init|=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|text
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codePointAt
argument_list|)
expr_stmt|;
name|i
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|codePointAt
argument_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|code
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|count
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|char
index|[]
condition|)
block|{
specifier|final
name|char
index|[]
name|text
init|=
operator|(
name|char
index|[]
operator|)
name|o
decl_stmt|;
return|return
name|contains
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|)
return|;
block|}
return|return
name|contains
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|char
index|[]
condition|)
block|{
return|return
name|add
argument_list|(
operator|(
name|char
index|[]
operator|)
name|o
argument_list|)
return|;
block|}
return|return
name|add
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns an unmodifiable {@link CharArraySet}. This allows to provide    * unmodifiable views of internal sets for "read-only" use.    *     * @param set    *          a set for which the unmodifiable set is returned.    * @return an new unmodifiable {@link CharArraySet}.    * @throws NullPointerException    *           if the given set is<code>null</code>.    */
DECL|method|unmodifiableSet
specifier|public
specifier|static
name|CharArraySet
name|unmodifiableSet
parameter_list|(
name|CharArraySet
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Given set is null"
argument_list|)
throw|;
if|if
condition|(
name|set
operator|==
name|EMPTY_SET
condition|)
return|return
name|EMPTY_SET
return|;
if|if
condition|(
name|set
operator|instanceof
name|UnmodifiableCharArraySet
condition|)
return|return
name|set
return|;
comment|/*      * Instead of delegating calls to the given set copy the low-level values to      * the unmodifiable Subclass      */
return|return
operator|new
name|UnmodifiableCharArraySet
argument_list|(
name|set
operator|.
name|matchVersion
argument_list|,
name|set
operator|.
name|entries
argument_list|,
name|set
operator|.
name|ignoreCase
argument_list|,
name|set
operator|.
name|count
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of the given set as a {@link CharArraySet}. If the given set    * is a {@link CharArraySet} the ignoreCase property will be preserved.    *     * @param set    *          a set to copy    * @return a copy of the given set as a {@link CharArraySet}. If the given set    *         is a {@link CharArraySet} the ignoreCase and matchVersion property will be    *         preserved.    * @deprecated use {@link #copy(Version, Set)} instead.    */
annotation|@
name|Deprecated
DECL|method|copy
specifier|public
specifier|static
name|CharArraySet
name|copy
parameter_list|(
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
name|EMPTY_SET
condition|)
return|return
name|EMPTY_SET
return|;
return|return
operator|(
name|set
operator|instanceof
name|CharArraySet
operator|)
condition|?
name|copy
argument_list|(
operator|(
name|CharArraySet
operator|)
name|set
argument_list|)
else|:
name|copy
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
name|set
argument_list|)
return|;
block|}
comment|/**    * Returns a copy of the given set as a {@link CharArraySet}. If the given set    * is a {@link CharArraySet} the ignoreCase property will be preserved.    *<p>    *<b>Note:</b> If you intend to create a copy of another {@link CharArraySet} where    * the {@link Version} of the source set differs from its copy    * {@link #CharArraySet(Version, Collection, boolean)} should be used instead.    * The {@link #copy(Version, Set)} will preserve the {@link Version} of the    * source set it is an instance of {@link CharArraySet}.    *</p>    *     * @param matchVersion    *          compatibility match version see<a href="#version">Version    *          note</a> above for details. This argument will be ignored if the    *          given set is a {@link CharArraySet}.    * @param set    *          a set to copy    * @return a copy of the given set as a {@link CharArraySet}. If the given set    *         is a {@link CharArraySet} the ignoreCase property as well as the    *         matchVersion will be of the given set will be preserved.    */
DECL|method|copy
specifier|public
specifier|static
name|CharArraySet
name|copy
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|set
parameter_list|)
block|{
if|if
condition|(
name|set
operator|==
name|EMPTY_SET
condition|)
return|return
name|EMPTY_SET
return|;
if|if
condition|(
name|set
operator|instanceof
name|CharArraySet
condition|)
block|{
specifier|final
name|CharArraySet
name|source
init|=
operator|(
name|CharArraySet
operator|)
name|set
decl_stmt|;
comment|// use fast path instead of iterating all values
comment|// this is even on very small sets ~10 times faster than iterating
specifier|final
name|char
index|[]
index|[]
name|entries
init|=
operator|new
name|char
index|[
name|source
operator|.
name|entries
operator|.
name|length
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|source
operator|.
name|entries
argument_list|,
literal|0
argument_list|,
name|entries
argument_list|,
literal|0
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|CharArraySet
argument_list|(
name|source
operator|.
name|matchVersion
argument_list|,
name|entries
argument_list|,
name|source
operator|.
name|ignoreCase
argument_list|,
name|source
operator|.
name|count
argument_list|)
return|;
block|}
return|return
operator|new
name|CharArraySet
argument_list|(
name|matchVersion
argument_list|,
name|set
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** The Iterator<String> for this set.  Strings are constructed on the fly, so    * use<code>nextCharArray</code> for more efficient access. */
DECL|class|CharArraySetIterator
specifier|public
class|class
name|CharArraySetIterator
implements|implements
name|Iterator
argument_list|<
name|String
argument_list|>
block|{
DECL|field|pos
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|next
name|char
index|[]
name|next
decl_stmt|;
DECL|method|CharArraySetIterator
name|CharArraySetIterator
parameter_list|()
block|{
name|goNext
argument_list|()
expr_stmt|;
block|}
DECL|method|goNext
specifier|private
name|void
name|goNext
parameter_list|()
block|{
name|next
operator|=
literal|null
expr_stmt|;
name|pos
operator|++
expr_stmt|;
while|while
condition|(
name|pos
operator|<
name|entries
operator|.
name|length
operator|&&
operator|(
name|next
operator|=
name|entries
index|[
name|pos
index|]
operator|)
operator|==
literal|null
condition|)
name|pos
operator|++
expr_stmt|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
comment|/** do not modify the returned char[] */
DECL|method|nextCharArray
specifier|public
name|char
index|[]
name|nextCharArray
parameter_list|()
block|{
name|char
index|[]
name|ret
init|=
name|next
decl_stmt|;
name|goNext
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/** Returns the next String, as a Set<String> would...      * use nextCharArray() for better efficiency. */
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|nextCharArray
argument_list|()
argument_list|)
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/** returns an iterator of new allocated Strings */
DECL|method|stringIterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|stringIterator
parameter_list|()
block|{
return|return
operator|new
name|CharArraySetIterator
argument_list|()
return|;
block|}
comment|/** returns an iterator of new allocated Strings, this method violates the Set interface */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|(
name|Iterator
operator|)
name|stringIterator
argument_list|()
return|;
block|}
comment|/**    * Efficient unmodifiable {@link CharArraySet}. This implementation does not    * delegate calls to a give {@link CharArraySet} like    * {@link Collections#unmodifiableSet(java.util.Set)} does. Instead is passes    * the internal representation of a {@link CharArraySet} to a super    * constructor and overrides all mutators.     */
DECL|class|UnmodifiableCharArraySet
specifier|private
specifier|static
specifier|final
class|class
name|UnmodifiableCharArraySet
extends|extends
name|CharArraySet
block|{
DECL|method|UnmodifiableCharArraySet
specifier|private
name|UnmodifiableCharArraySet
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|char
index|[]
index|[]
name|entries
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|entries
argument_list|,
name|ignoreCase
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addAll
specifier|public
name|boolean
name|addAll
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|coll
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|char
index|[]
name|text
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|String
name|text
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class
end_unit
