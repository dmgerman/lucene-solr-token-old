begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInput
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Externalizable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/** Represents byte[], as a slice (offset + length) into an  *  existing byte[].  *  *  @lucene.experimental */
end_comment
begin_class
DECL|class|BytesRef
specifier|public
specifier|final
class|class
name|BytesRef
implements|implements
name|Comparable
argument_list|<
name|BytesRef
argument_list|>
implements|,
name|Externalizable
block|{
DECL|field|HASH_PRIME
specifier|static
specifier|final
name|int
name|HASH_PRIME
init|=
literal|31
decl_stmt|;
DECL|field|EMPTY_BYTES
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/** The contents of the BytesRef. Should never be {@code null}. */
DECL|field|bytes
specifier|public
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/** Offset of first valid byte. */
DECL|field|offset
specifier|public
name|int
name|offset
decl_stmt|;
comment|/** Length of used bytes. */
DECL|field|length
specifier|public
name|int
name|length
decl_stmt|;
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|()
block|{
name|bytes
operator|=
name|EMPTY_BYTES
expr_stmt|;
block|}
comment|/** This instance will directly reference bytes w/o making a copy.    * bytes should not be null.    */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
assert|assert
name|bytes
operator|!=
literal|null
assert|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/** This instance will directly reference bytes w/o making a copy.    * bytes should not be null */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
assert|assert
name|bytes
operator|!=
literal|null
assert|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/**    * @param text Initialize the byte[] from the UTF8 bytes    * for the provided Sring.  This must be well-formed    * unicode text, with no unpaired surrogates or U+FFFF.    */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|copy
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param text Initialize the byte[] from the UTF8 bytes    * for the provided array.  This must be well-formed    * unicode text, with no unpaired surrogates or U+FFFF.    */
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
argument_list|(
name|length
operator|*
literal|4
argument_list|)
expr_stmt|;
name|copy
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|BytesRef
specifier|public
name|BytesRef
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|copy
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
comment|/* // maybe?   public BytesRef(BytesRef other, boolean shallow) {     this();     if (shallow) {       offset = other.offset;       length = other.length;       bytes = other.bytes;     } else {       copy(other);     }   }   */
comment|/**    * Copies the UTF8 bytes for this string.    *     * @param text Must be well-formed unicode text, with no    * unpaired surrogates or invalid UTF16 code units.    */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copies the UTF8 bytes for this string.    *     * @param text Must be well-formed unicode text, with no    * unpaired surrogates or invalid UTF16 code units.    */
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|text
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|bytesEquals
specifier|public
name|boolean
name|bytesEquals
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
name|other
operator|.
name|length
condition|)
block|{
name|int
name|otherUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|otherBytes
init|=
name|other
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|upto
init|=
name|offset
init|;
name|upto
operator|<
name|end
condition|;
name|upto
operator|++
operator|,
name|otherUpto
operator|++
control|)
block|{
if|if
condition|(
name|bytes
index|[
name|upto
index|]
operator|!=
name|otherBytes
index|[
name|otherUpto
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|sliceEquals
specifier|private
name|boolean
name|sliceEquals
parameter_list|(
name|BytesRef
name|other
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|length
operator|-
name|pos
operator|<
name|other
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
name|offset
operator|+
name|pos
decl_stmt|;
name|int
name|j
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|k
init|=
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|k
condition|)
block|{
if|if
condition|(
name|bytes
index|[
name|i
operator|++
index|]
operator|!=
name|other
operator|.
name|bytes
index|[
name|j
operator|++
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|startsWith
specifier|public
name|boolean
name|startsWith
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
return|return
name|sliceEquals
argument_list|(
name|other
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|endsWith
specifier|public
name|boolean
name|endsWith
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
return|return
name|sliceEquals
argument_list|(
name|other
argument_list|,
name|length
operator|-
name|other
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Calculates the hash code as required by TermsHash during indexing.    *<p>It is defined as:    *<pre>    *  int hash = 0;    *  for (int i = offset; i&lt; offset + length; i++) {    *    hash = 31*hash + bytes[i];    *  }    *</pre>    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|HASH_PRIME
operator|*
name|result
operator|+
name|bytes
index|[
name|i
index|]
expr_stmt|;
block|}
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
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|bytesEquals
argument_list|(
operator|(
name|BytesRef
operator|)
name|other
argument_list|)
return|;
block|}
comment|/** Interprets stored bytes as UTF8 bytes, returning the    *  resulting string */
DECL|method|utf8ToString
specifier|public
name|String
name|utf8ToString
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
comment|// should not happen -- UTF8 is presumably supported
comment|// by all JREs
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|uee
argument_list|)
throw|;
block|}
block|}
comment|/** Returns hex encoded bytes, eg [0x6c 0x75 0x63 0x65 0x6e 0x65] */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
name|offset
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|bytes
index|[
name|i
index|]
operator|&
literal|0xff
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|<
name|other
operator|.
name|length
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|other
operator|.
name|length
index|]
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|bytes
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
name|int
name|newLen
init|=
name|length
operator|+
name|other
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|<
name|newLen
condition|)
block|{
name|byte
index|[]
name|newBytes
init|=
operator|new
name|byte
index|[
name|newLen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|newBytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|bytes
operator|=
name|newBytes
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|bytes
argument_list|,
name|other
operator|.
name|offset
argument_list|,
name|bytes
argument_list|,
name|length
operator|+
name|offset
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|=
name|newLen
expr_stmt|;
block|}
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|newLength
parameter_list|)
block|{
name|bytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|bytes
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
comment|/** Unsigned byte order comparison */
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|BytesRef
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
return|return
literal|0
return|;
specifier|final
name|byte
index|[]
name|aBytes
init|=
name|this
operator|.
name|bytes
decl_stmt|;
name|int
name|aUpto
init|=
name|this
operator|.
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|bBytes
init|=
name|other
operator|.
name|bytes
decl_stmt|;
name|int
name|bUpto
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|aStop
init|=
name|aUpto
operator|+
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|length
argument_list|,
name|other
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|int
name|aByte
init|=
name|aBytes
index|[
name|aUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|bByte
init|=
name|bBytes
index|[
name|bUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|diff
init|=
name|aByte
operator|-
name|bByte
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
return|return
name|diff
return|;
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|this
operator|.
name|length
operator|-
name|other
operator|.
name|length
return|;
block|}
DECL|field|utf8SortedAsUnicodeSortOrder
specifier|private
specifier|final
specifier|static
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|utf8SortedAsUnicodeSortOrder
init|=
operator|new
name|UTF8SortedAsUnicodeComparator
argument_list|()
decl_stmt|;
DECL|method|getUTF8SortedAsUnicodeComparator
specifier|public
specifier|static
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getUTF8SortedAsUnicodeComparator
parameter_list|()
block|{
return|return
name|utf8SortedAsUnicodeSortOrder
return|;
block|}
DECL|class|UTF8SortedAsUnicodeComparator
specifier|private
specifier|static
class|class
name|UTF8SortedAsUnicodeComparator
implements|implements
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
block|{
comment|// Only singleton
DECL|method|UTF8SortedAsUnicodeComparator
specifier|private
name|UTF8SortedAsUnicodeComparator
parameter_list|()
block|{}
empty_stmt|;
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|a
parameter_list|,
name|BytesRef
name|b
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|aBytes
init|=
name|a
operator|.
name|bytes
decl_stmt|;
name|int
name|aUpto
init|=
name|a
operator|.
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|bBytes
init|=
name|b
operator|.
name|bytes
decl_stmt|;
name|int
name|bUpto
init|=
name|b
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|aStop
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|b
operator|.
name|length
condition|)
block|{
name|aStop
operator|=
name|aUpto
operator|+
name|a
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|aStop
operator|=
name|aUpto
operator|+
name|b
operator|.
name|length
expr_stmt|;
block|}
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|int
name|aByte
init|=
name|aBytes
index|[
name|aUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|bByte
init|=
name|bBytes
index|[
name|bUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|diff
init|=
name|aByte
operator|-
name|bByte
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|a
operator|.
name|length
operator|-
name|b
operator|.
name|length
return|;
block|}
block|}
DECL|field|utf8SortedAsUTF16SortOrder
specifier|private
specifier|final
specifier|static
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|utf8SortedAsUTF16SortOrder
init|=
operator|new
name|UTF8SortedAsUTF16Comparator
argument_list|()
decl_stmt|;
DECL|method|getUTF8SortedAsUTF16Comparator
specifier|public
specifier|static
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getUTF8SortedAsUTF16Comparator
parameter_list|()
block|{
return|return
name|utf8SortedAsUTF16SortOrder
return|;
block|}
DECL|class|UTF8SortedAsUTF16Comparator
specifier|private
specifier|static
class|class
name|UTF8SortedAsUTF16Comparator
implements|implements
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
block|{
comment|// Only singleton
DECL|method|UTF8SortedAsUTF16Comparator
specifier|private
name|UTF8SortedAsUTF16Comparator
parameter_list|()
block|{}
empty_stmt|;
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|BytesRef
name|a
parameter_list|,
name|BytesRef
name|b
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|aBytes
init|=
name|a
operator|.
name|bytes
decl_stmt|;
name|int
name|aUpto
init|=
name|a
operator|.
name|offset
decl_stmt|;
specifier|final
name|byte
index|[]
name|bBytes
init|=
name|b
operator|.
name|bytes
decl_stmt|;
name|int
name|bUpto
init|=
name|b
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|aStop
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|b
operator|.
name|length
condition|)
block|{
name|aStop
operator|=
name|aUpto
operator|+
name|a
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|aStop
operator|=
name|aUpto
operator|+
name|b
operator|.
name|length
expr_stmt|;
block|}
while|while
condition|(
name|aUpto
operator|<
name|aStop
condition|)
block|{
name|int
name|aByte
init|=
name|aBytes
index|[
name|aUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
name|int
name|bByte
init|=
name|bBytes
index|[
name|bUpto
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
name|aByte
operator|!=
name|bByte
condition|)
block|{
comment|// See http://icu-project.org/docs/papers/utf16_code_point_order.html#utf-8-in-utf-16-order
comment|// We know the terms are not equal, but, we may
comment|// have to carefully fixup the bytes at the
comment|// difference to match UTF16's sort order:
comment|// NOTE: instead of moving supplementary code points (0xee and 0xef) to the unused 0xfe and 0xff,
comment|// we move them to the unused 0xfc and 0xfd [reserved for future 6-byte character sequences]
comment|// this reserves 0xff for preflex's term reordering (surrogate dance), and if unicode grows such
comment|// that 6-byte sequences are needed we have much bigger problems anyway.
if|if
condition|(
name|aByte
operator|>=
literal|0xee
operator|&&
name|bByte
operator|>=
literal|0xee
condition|)
block|{
if|if
condition|(
operator|(
name|aByte
operator|&
literal|0xfe
operator|)
operator|==
literal|0xee
condition|)
block|{
name|aByte
operator|+=
literal|0xe
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|bByte
operator|&
literal|0xfe
operator|)
operator|==
literal|0xee
condition|)
block|{
name|bByte
operator|+=
literal|0xe
expr_stmt|;
block|}
block|}
return|return
name|aByte
operator|-
name|bByte
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|a
operator|.
name|length
operator|-
name|b
operator|.
name|length
return|;
block|}
block|}
DECL|method|writeExternal
specifier|public
name|void
name|writeExternal
parameter_list|(
name|ObjectOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readExternal
specifier|public
name|void
name|readExternal
parameter_list|(
name|ObjectInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|length
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|in
operator|.
name|read
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|=
name|EMPTY_BYTES
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
