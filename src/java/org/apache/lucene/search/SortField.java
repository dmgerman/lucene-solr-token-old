begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|TermEnum
import|;
end_import
begin_comment
comment|/**  * Stores information about how to sort documents by terms in an individual  * field.  Fields must be indexed in order to sort by them.  *  *<p>Created: Feb 11, 2004 1:25:29 PM  *  * @since   lucene 1.4  * @version $Id$  * @see Sort  */
end_comment
begin_class
DECL|class|SortField
specifier|public
class|class
name|SortField
implements|implements
name|Serializable
block|{
comment|/** Sort by document score (relevancy).  Sort values are Float and higher    * values are at the front. */
DECL|field|SCORE
specifier|public
specifier|static
specifier|final
name|int
name|SCORE
init|=
literal|0
decl_stmt|;
comment|/** Sort by document number (index order).  Sort values are Integer and lower    * values are at the front. */
DECL|field|DOC
specifier|public
specifier|static
specifier|final
name|int
name|DOC
init|=
literal|1
decl_stmt|;
comment|/** Guess type of sort based on field contents.  A regular expression is used    * to look at the first term indexed for the field and determine if it    * represents an integer number, a floating point number, or just arbitrary    * string characters.    * @deprecated Please specify the exact type, instead.*/
DECL|field|AUTO
specifier|public
specifier|static
specifier|final
name|int
name|AUTO
init|=
literal|2
decl_stmt|;
comment|/** Sort using term values as Strings.  Sort values are String and lower    * values are at the front. */
DECL|field|STRING
specifier|public
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|3
decl_stmt|;
comment|/** Sort using term values as encoded Integers.  Sort values are Integer and    * lower values are at the front. */
DECL|field|INT
specifier|public
specifier|static
specifier|final
name|int
name|INT
init|=
literal|4
decl_stmt|;
comment|/** Sort using term values as encoded Floats.  Sort values are Float and    * lower values are at the front. */
DECL|field|FLOAT
specifier|public
specifier|static
specifier|final
name|int
name|FLOAT
init|=
literal|5
decl_stmt|;
comment|/** Sort using term values as encoded Longs.  Sort values are Long and    * lower values are at the front. */
DECL|field|LONG
specifier|public
specifier|static
specifier|final
name|int
name|LONG
init|=
literal|6
decl_stmt|;
comment|/** Sort using term values as encoded Doubles.  Sort values are Double and    * lower values are at the front. */
DECL|field|DOUBLE
specifier|public
specifier|static
specifier|final
name|int
name|DOUBLE
init|=
literal|7
decl_stmt|;
comment|/** Sort using term values as encoded Shorts.  Sort values are Short and    * lower values are at the front. */
DECL|field|SHORT
specifier|public
specifier|static
specifier|final
name|int
name|SHORT
init|=
literal|8
decl_stmt|;
comment|/** Sort using a custom Comparator.  Sort values are any Comparable and    * sorting is done according to natural order. */
DECL|field|CUSTOM
specifier|public
specifier|static
specifier|final
name|int
name|CUSTOM
init|=
literal|9
decl_stmt|;
comment|/** Sort using term values as encoded Bytes.  Sort values are Byte and    * lower values are at the front. */
DECL|field|BYTE
specifier|public
specifier|static
specifier|final
name|int
name|BYTE
init|=
literal|10
decl_stmt|;
comment|/** Sort using term values as Strings, but comparing by    * value (using String.compareTo) for all comparisons.    * This is typically slower than {@link #STRING}, which    * uses ordinals to do the sorting. */
DECL|field|STRING_VAL
specifier|public
specifier|static
specifier|final
name|int
name|STRING_VAL
init|=
literal|11
decl_stmt|;
comment|// IMPLEMENTATION NOTE: the FieldCache.STRING_INDEX is in the same "namespace"
comment|// as the above static int values.  Any new values must not have the same value
comment|// as FieldCache.STRING_INDEX.
comment|/** Represents sorting by document score (relevancy). */
DECL|field|FIELD_SCORE
specifier|public
specifier|static
specifier|final
name|SortField
name|FIELD_SCORE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SCORE
argument_list|)
decl_stmt|;
comment|/** Represents sorting by document number (index order). */
DECL|field|FIELD_DOC
specifier|public
specifier|static
specifier|final
name|SortField
name|FIELD_DOC
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|DOC
argument_list|)
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|type
specifier|private
name|int
name|type
init|=
name|AUTO
decl_stmt|;
comment|// defaults to determining type dynamically
DECL|field|locale
specifier|private
name|Locale
name|locale
decl_stmt|;
comment|// defaults to "natural order" (no Locale)
DECL|field|reverse
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
comment|// defaults to natural order
DECL|field|factory
specifier|private
name|SortComparatorSource
name|factory
decl_stmt|;
DECL|field|parser
specifier|private
name|FieldCache
operator|.
name|Parser
name|parser
decl_stmt|;
comment|// Used for CUSTOM sort
DECL|field|comparatorSource
specifier|private
name|FieldComparatorSource
name|comparatorSource
decl_stmt|;
DECL|field|useLegacy
specifier|private
name|boolean
name|useLegacy
init|=
literal|false
decl_stmt|;
comment|// remove in Lucene 3.0
comment|/** Creates a sort by terms in the given field where the type of term value    * is determined dynamically ({@link #AUTO AUTO}).    * @param field Name of field to sort by, cannot be    *<code>null</code>.    * @deprecated Please specify the exact type instead.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|AUTO
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field where    * the type of term value is determined dynamically ({@link #AUTO AUTO}).    * @param field Name of field to sort by, cannot be<code>null</code>.    * @param reverse True if natural order should be reversed.    * @deprecated Please specify the exact type instead.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|AUTO
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field with the type of term    * values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field with the    * type of term values explicitly given.    * @param field  Name of field to sort by.  Can be<code>null</code> if    *<code>type</code> is SCORE or DOC.    * @param type   Type of values in the terms.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field, parsed    * to numeric values using a custom {@link FieldCache.Parser}.    * @param field  Name of field to sort by.  Must not be null.    * @param parser Instance of a {@link FieldCache.Parser},    *  which must subclass one of the existing numeric    *  parsers from {@link FieldCache} or {@link    *  ExtendedFieldCache}. Sort type is inferred by testing    *  which numeric parser the parser subclasses.    * @throws IllegalArgumentException if the parser fails to    *  subclass an existing numeric parser, or field is null    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|Parser
name|parser
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|parser
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field, parsed    * to numeric values using a custom {@link FieldCache.Parser}.    * @param field  Name of field to sort by.  Must not be null.    * @param parser Instance of a {@link FieldCache.Parser},    *  which must subclass one of the existing numeric    *  parsers from {@link FieldCache} or {@link    *  ExtendedFieldCache}. Sort type is inferred by testing    *  which numeric parser the parser subclasses.    * @param reverse True if natural order should be reversed.    * @throws IllegalArgumentException if the parser fails to    *  subclass an existing numeric parser, or field is null    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|Parser
name|parser
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|IntParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|INT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|FloatParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|FLOAT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|ShortParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|SHORT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|FieldCache
operator|.
name|ByteParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|BYTE
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|ExtendedFieldCache
operator|.
name|LongParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|parser
operator|instanceof
name|ExtendedFieldCache
operator|.
name|DoubleParser
condition|)
name|initFieldType
argument_list|(
name|field
argument_list|,
name|DOUBLE
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Parser instance does not subclass existing numeric parser from FieldCache or ExtendedFieldCache (got "
operator|+
name|parser
operator|+
literal|")"
argument_list|)
throw|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/** Creates a sort by terms in the given field sorted    * according to the given locale.    * @param field  Name of field to sort by, cannot be<code>null</code>.    * @param locale Locale of values in the field.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, by terms in the given field sorted    * according to the given locale.    * @param field  Name of field to sort by, cannot be<code>null</code>.    * @param locale Locale of values in the field.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
block|}
comment|/** Creates a sort with a custom comparison function.    * @param field Name of field to sort by; cannot be<code>null</code>.    * @param comparator Returns a comparator for sorting hits.    * @deprecated use SortField (String field, FieldComparatorSource comparator)    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|SortComparatorSource
name|comparator
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|CUSTOM
argument_list|)
expr_stmt|;
name|setUseLegacySearch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|comparator
expr_stmt|;
block|}
comment|/** Creates a sort with a custom comparison function.    * @param field Name of field to sort by; cannot be<code>null</code>.    * @param comparator Returns a comparator for sorting hits.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldComparatorSource
name|comparator
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|CUSTOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparatorSource
operator|=
name|comparator
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, with a custom comparison function.    * @param field Name of field to sort by; cannot be<code>null</code>.    * @param comparator Returns a comparator for sorting hits.    * @param reverse True if natural order should be reversed.    * @deprecated use SortField (String field, FieldComparatorSource comparator, boolean reverse)    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|SortComparatorSource
name|comparator
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|CUSTOM
argument_list|)
expr_stmt|;
name|setUseLegacySearch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|comparator
expr_stmt|;
block|}
comment|/** Creates a sort, possibly in reverse, with a custom comparison function.    * @param field Name of field to sort by; cannot be<code>null</code>.    * @param comparator Returns a comparator for sorting hits.    * @param reverse True if natural order should be reversed.    */
DECL|method|SortField
specifier|public
name|SortField
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldComparatorSource
name|comparator
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
name|initFieldType
argument_list|(
name|field
argument_list|,
name|CUSTOM
argument_list|)
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|comparatorSource
operator|=
name|comparator
expr_stmt|;
block|}
comment|// Sets field& type, and ensures field is not NULL unless
comment|// type is SCORE or DOC
DECL|method|initFieldType
specifier|private
name|void
name|initFieldType
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|!=
name|SCORE
operator|&&
name|type
operator|!=
name|DOC
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field can only be null when type is SCORE or DOC"
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the name of the field.  Could return<code>null</code>    * if the sort is by SCORE or DOC.    * @return Name of field, possibly<code>null</code>.    */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/** Returns the type of contents in the field.    * @return One of the constants SCORE, DOC, AUTO, STRING, INT or FLOAT.    */
DECL|method|getType
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/** Returns the Locale by which term values are interpreted.    * May return<code>null</code> if no Locale was specified.    * @return Locale, or<code>null</code>.    */
DECL|method|getLocale
specifier|public
name|Locale
name|getLocale
parameter_list|()
block|{
return|return
name|locale
return|;
block|}
comment|/** Returns the instance of a {@link FieldCache} parser that fits to the given sort type.    * May return<code>null</code> if no parser was specified. Sorting is using the default parser then.    * @return An instance of a {@link FieldCache} parser, or<code>null</code>.    */
DECL|method|getParser
specifier|public
name|FieldCache
operator|.
name|Parser
name|getParser
parameter_list|()
block|{
return|return
name|parser
return|;
block|}
comment|/** Returns whether the sort should be reversed.    * @return  True if natural order should be reversed.    */
DECL|method|getReverse
specifier|public
name|boolean
name|getReverse
parameter_list|()
block|{
return|return
name|reverse
return|;
block|}
comment|/**    * @deprecated use {@link #getComparatorSource()}    */
DECL|method|getFactory
specifier|public
name|SortComparatorSource
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
DECL|method|getComparatorSource
specifier|public
name|FieldComparatorSource
name|getComparatorSource
parameter_list|()
block|{
return|return
name|comparatorSource
return|;
block|}
comment|/**    * Use legacy IndexSearch implementation: search with a MultiSegmentReader rather    * than passing a single hit collector to multiple SegmentReaders.    *     * @param legacy true for legacy behavior    * @deprecated will be removed in Lucene 3.0.    */
DECL|method|setUseLegacySearch
specifier|public
name|void
name|setUseLegacySearch
parameter_list|(
name|boolean
name|legacy
parameter_list|)
block|{
name|this
operator|.
name|useLegacy
operator|=
name|legacy
expr_stmt|;
block|}
comment|/**    * @return if true, IndexSearch will use legacy sorting search implementation.    * eg. multiple Priority Queues.    * @deprecated will be removed in Lucene 3.0.    */
DECL|method|getUseLegacySearch
specifier|public
name|boolean
name|getUseLegacySearch
parameter_list|()
block|{
return|return
name|this
operator|.
name|useLegacy
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SCORE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<score>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOC
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<doc>"
argument_list|)
expr_stmt|;
break|break;
case|case
name|AUTO
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<auto: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<string: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING_VAL
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<string_val: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<byte: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHORT
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<short: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|INT
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<int: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<long: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<float: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<double: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
case|case
name|CUSTOM
case|:
name|buffer
operator|.
name|append
argument_list|(
literal|"<custom:\""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\": "
argument_list|)
operator|.
name|append
argument_list|(
name|factory
argument_list|)
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
break|break;
default|default:
name|buffer
operator|.
name|append
argument_list|(
literal|"<???: \""
argument_list|)
operator|.
name|append
argument_list|(
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|locale
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|parser
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this.  If a    *  {@link SortComparatorSource} (deprecated) or {@link    *  FieldCache.Parser} was provided, it must properly    *  implement equals (unless a singleton is always used). */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SortField
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|SortField
name|other
init|=
operator|(
name|SortField
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|other
operator|.
name|field
operator|==
name|this
operator|.
name|field
comment|// field is always interned
operator|&&
name|other
operator|.
name|type
operator|==
name|this
operator|.
name|type
operator|&&
name|other
operator|.
name|reverse
operator|==
name|this
operator|.
name|reverse
operator|&&
operator|(
name|other
operator|.
name|locale
operator|==
literal|null
condition|?
name|this
operator|.
name|locale
operator|==
literal|null
else|:
name|other
operator|.
name|locale
operator|.
name|equals
argument_list|(
name|this
operator|.
name|locale
argument_list|)
operator|)
operator|&&
operator|(
name|other
operator|.
name|factory
operator|==
literal|null
condition|?
name|this
operator|.
name|factory
operator|==
literal|null
else|:
name|other
operator|.
name|factory
operator|.
name|equals
argument_list|(
name|this
operator|.
name|factory
argument_list|)
operator|)
operator|&&
operator|(
name|other
operator|.
name|comparatorSource
operator|==
literal|null
condition|?
name|this
operator|.
name|comparatorSource
operator|==
literal|null
else|:
name|other
operator|.
name|comparatorSource
operator|.
name|equals
argument_list|(
name|this
operator|.
name|comparatorSource
argument_list|)
operator|)
operator|&&
operator|(
name|other
operator|.
name|parser
operator|==
literal|null
condition|?
name|this
operator|.
name|parser
operator|==
literal|null
else|:
name|other
operator|.
name|parser
operator|.
name|equals
argument_list|(
name|this
operator|.
name|parser
argument_list|)
operator|)
operator|)
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this.  If a    *  {@link SortComparatorSource} (deprecated) or {@link    *  FieldCache.Parser} was provided, it must properly    *  implement hashCode (unless a singleton is always    *  used). */
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|type
operator|^
literal|0x346565dd
operator|+
name|Boolean
operator|.
name|valueOf
argument_list|(
name|reverse
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|^
literal|0xaf5998bb
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|field
operator|.
name|hashCode
argument_list|()
operator|^
literal|0xff5685dd
expr_stmt|;
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|locale
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x08150815
expr_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|factory
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x34987555
expr_stmt|;
if|if
condition|(
name|comparatorSource
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|comparatorSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
name|hash
operator|+=
name|parser
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x3aaf56ff
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/** Returns the {@link FieldComparator} to use for sorting.    * @param numHits number of top hits the queue will store    * @param sortPos position of this SortField within {@link    *   Sort}.  The comparator is primary if sortPos==0,    *   secondary if sortPos==1, etc.  Some comparators can    *   optimize themselves when they are the primary sort.    * @param reversed True if the SortField is reversed    * @return {@link FieldComparator} to use when sorting    */
DECL|method|getComparator
specifier|public
name|FieldComparator
name|getComparator
parameter_list|(
specifier|final
name|int
name|numHits
parameter_list|,
specifier|final
name|int
name|sortPos
parameter_list|,
specifier|final
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|locale
operator|!=
literal|null
condition|)
block|{
comment|// TODO: it'd be nice to allow FieldCache.getStringIndex
comment|// to optionally accept a Locale so sorting could then use
comment|// the faster StringComparator impls
return|return
operator|new
name|FieldComparator
operator|.
name|StringComparatorLocale
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|locale
argument_list|)
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|SortField
operator|.
name|SCORE
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|RelevanceComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|SortField
operator|.
name|DOC
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|DocComparator
argument_list|(
name|numHits
argument_list|)
return|;
case|case
name|SortField
operator|.
name|INT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|IntComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
return|;
case|case
name|SortField
operator|.
name|FLOAT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|FloatComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
return|;
case|case
name|SortField
operator|.
name|LONG
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|LongComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
return|;
case|case
name|SortField
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|DoubleComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
return|;
case|case
name|SortField
operator|.
name|BYTE
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|ByteComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
return|;
case|case
name|SortField
operator|.
name|SHORT
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|ShortComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
return|;
case|case
name|SortField
operator|.
name|CUSTOM
case|:
assert|assert
name|factory
operator|==
literal|null
operator|&&
name|comparatorSource
operator|!=
literal|null
assert|;
return|return
name|comparatorSource
operator|.
name|newComparator
argument_list|(
name|field
argument_list|,
name|numHits
argument_list|,
name|sortPos
argument_list|,
name|reversed
argument_list|)
return|;
case|case
name|SortField
operator|.
name|STRING
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|StringOrdValComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|,
name|sortPos
argument_list|,
name|reversed
argument_list|)
return|;
case|case
name|SortField
operator|.
name|STRING_VAL
case|:
return|return
operator|new
name|FieldComparator
operator|.
name|StringValComparator
argument_list|(
name|numHits
argument_list|,
name|field
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal sort type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/** Attempts to detect the given field type for an IndexReader. */
DECL|method|detectFieldType
specifier|static
name|int
name|detectFieldType
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldKey
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|field
init|=
name|fieldKey
operator|.
name|intern
argument_list|()
decl_stmt|;
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no terms in field "
operator|+
name|field
operator|+
literal|" - cannot determine sort type"
argument_list|)
throw|;
block|}
name|int
name|ret
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|String
name|termtext
init|=
name|term
operator|.
name|text
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|/**          * Java 1.4 level code:           if (pIntegers.matcher(termtext).matches())          return IntegerSortedHitQueue.comparator (reader, enumerator, field);           else if (pFloats.matcher(termtext).matches())          return FloatSortedHitQueue.comparator (reader, enumerator, field);          */
comment|// Java 1.3 level code:
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|SortField
operator|.
name|INT
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe1
parameter_list|)
block|{
try|try
block|{
name|Long
operator|.
name|parseLong
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|SortField
operator|.
name|LONG
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe2
parameter_list|)
block|{
try|try
block|{
name|Float
operator|.
name|parseFloat
argument_list|(
name|termtext
argument_list|)
expr_stmt|;
name|ret
operator|=
name|SortField
operator|.
name|FLOAT
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe3
parameter_list|)
block|{
name|ret
operator|=
name|SortField
operator|.
name|STRING
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" does not appear to be indexed"
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
