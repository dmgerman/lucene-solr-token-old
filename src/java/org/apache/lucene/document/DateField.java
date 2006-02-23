begin_unit
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|search
operator|.
name|PrefixQuery
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|RangeQuery
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
begin_comment
comment|/**  * Provides support for converting dates to strings and vice-versa.  * The strings are structured so that lexicographic sorting orders by date,  * which makes them suitable for use as field values and search terms.  *   *<P>Note that this class saves dates with millisecond granularity,  * which is bad for {@link RangeQuery} and {@link PrefixQuery}, as those  * queries are expanded to a BooleanQuery with a potentially large number   * of terms when searching. Thus you might want to use  * {@link DateTools} instead.  *   *<P>  * Note: dates before 1970 cannot be used, and therefore cannot be  * indexed when using this class. See {@link DateTools} for an  * alternative without such a limitation.  *   * @deprecated If you build a new index, use {@link DateTools} instead. For   *  existing indices you can continue using this class, as it will not be   *  removed in the near future despite being deprecated.  */
end_comment
begin_class
DECL|class|DateField
specifier|public
class|class
name|DateField
block|{
DECL|method|DateField
specifier|private
name|DateField
parameter_list|()
block|{}
comment|// make date strings long enough to last a millenium
DECL|field|DATE_LEN
specifier|private
specifier|static
name|int
name|DATE_LEN
init|=
name|Long
operator|.
name|toString
argument_list|(
literal|1000L
operator|*
literal|365
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|method|MIN_DATE_STRING
specifier|public
specifier|static
name|String
name|MIN_DATE_STRING
parameter_list|()
block|{
return|return
name|timeToString
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|MAX_DATE_STRING
specifier|public
specifier|static
name|String
name|MAX_DATE_STRING
parameter_list|()
block|{
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|DATE_LEN
index|]
decl_stmt|;
name|char
name|c
init|=
name|Character
operator|.
name|forDigit
argument_list|(
name|Character
operator|.
name|MAX_RADIX
operator|-
literal|1
argument_list|,
name|Character
operator|.
name|MAX_RADIX
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
name|DATE_LEN
condition|;
name|i
operator|++
control|)
name|buffer
index|[
name|i
index|]
operator|=
name|c
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|/**    * Converts a Date to a string suitable for indexing.    * @throws RuntimeException if the date specified in the    * method argument is before 1970    */
DECL|method|dateToString
specifier|public
specifier|static
name|String
name|dateToString
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
return|return
name|timeToString
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Converts a millisecond time to a string suitable for indexing.    * @throws RuntimeException if the time specified in the    * method argument is negative, that is, before 1970    */
DECL|method|timeToString
specifier|public
specifier|static
name|String
name|timeToString
parameter_list|(
name|long
name|time
parameter_list|)
block|{
if|if
condition|(
name|time
operator|<
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"time '"
operator|+
name|time
operator|+
literal|"' is too early, must be>= 0"
argument_list|)
throw|;
name|String
name|s
init|=
name|Long
operator|.
name|toString
argument_list|(
name|time
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
name|DATE_LEN
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"time '"
operator|+
name|time
operator|+
literal|"' is too late, length of string "
operator|+
literal|"representation must be<= "
operator|+
name|DATE_LEN
argument_list|)
throw|;
comment|// Pad with leading zeros
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
name|DATE_LEN
condition|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|s
argument_list|)
decl_stmt|;
while|while
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|<
name|DATE_LEN
condition|)
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|s
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/** Converts a string-encoded date into a millisecond time. */
DECL|method|stringToTime
specifier|public
specifier|static
name|long
name|stringToTime
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
return|;
block|}
comment|/** Converts a string-encoded date into a Date object. */
DECL|method|stringToDate
specifier|public
specifier|static
name|Date
name|stringToDate
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|stringToTime
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
