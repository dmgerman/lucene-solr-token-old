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
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_comment
comment|/**  * Provides support for converting dates to strings and vice-versa.  * The strings are structured so that lexicographic sorting orders   * them by date, which makes them suitable for use as field values   * and search terms.  *   *<P>This class also helps you to limit the resolution of your dates. Do not  * save dates with a finer resolution than you really need, as then  * RangeQuery and PrefixQuery will require more memory and become slower.  *   *<P>Compared to {@link DateField} the strings generated by the methods  * in this class take slightly more space, unless your selected resolution  * is set to<code>Resolution.DAY</code> or lower.  */
end_comment
begin_class
DECL|class|DateTools
specifier|public
class|class
name|DateTools
block|{
DECL|method|DateTools
specifier|private
name|DateTools
parameter_list|()
block|{}
comment|/**    * Converts a Date to a string suitable for indexing.    *     * @param date the date to be converted    * @param resolution the desired resolution, see    *  {@link #round(Date, DateTools.Resolution)}    * @return a string in format<code>yyyyMMddHHmmssSSS</code> or shorter,    *  depeding on<code>resolution</code>    */
DECL|method|dateToString
specifier|public
specifier|static
name|String
name|dateToString
parameter_list|(
name|Date
name|date
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
return|return
name|timeToString
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|resolution
argument_list|)
return|;
block|}
comment|/**    * Converts a millisecond time to a string suitable for indexing.    *     * @param time the date expressed as milliseconds since January 1, 1970, 00:00:00 GMT    * @param resolution the desired resolution, see    *  {@link #round(long, DateTools.Resolution)}    * @return a string in format<code>yyyyMMddHHmmssSSS</code> or shorter,    *  depeding on<code>resolution</code>    */
DECL|method|timeToString
specifier|public
specifier|static
name|String
name|timeToString
parameter_list|(
name|long
name|time
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|round
argument_list|(
name|time
argument_list|,
name|resolution
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|YEAR
condition|)
block|{
name|pattern
operator|=
literal|"yyyy"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|MONTH
condition|)
block|{
name|pattern
operator|=
literal|"yyyyMM"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|DAY
condition|)
block|{
name|pattern
operator|=
literal|"yyyyMMdd"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|HOUR
condition|)
block|{
name|pattern
operator|=
literal|"yyyyMMddHH"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|MINUTE
condition|)
block|{
name|pattern
operator|=
literal|"yyyyMMddHHmm"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|SECOND
condition|)
block|{
name|pattern
operator|=
literal|"yyyyMMddHHmmss"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|MILLISECOND
condition|)
block|{
name|pattern
operator|=
literal|"yyyyMMddHHmmssSSS"
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown resolution "
operator|+
name|resolution
argument_list|)
throw|;
block|}
name|sdf
operator|.
name|applyPattern
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
return|return
name|sdf
operator|.
name|format
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Converts a string produced by<code>timeToString</code> or    *<code>dateToString</code> back to a time, represented as the    * number of milliseconds since January 1, 1970, 00:00:00 GMT.    *     * @param dateString the date string to be converted    * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT    * @throws ParseException if<code>dateString</code> is not in the     *  expected format     */
DECL|method|stringToTime
specifier|public
specifier|static
name|long
name|stringToTime
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|stringToDate
argument_list|(
name|dateString
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
block|}
comment|/**    * Converts a string produced by<code>timeToString</code> or    *<code>dateToString</code> back to a time, represented as a    * Date object.    *     * @param dateString the date string to be converted    * @return the parsed time as a Date object     * @throws ParseException if<code>dateString</code> is not in the     *  expected format     */
DECL|method|stringToDate
specifier|public
specifier|static
name|Date
name|stringToDate
parameter_list|(
name|String
name|dateString
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|pattern
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|4
condition|)
name|pattern
operator|=
literal|"yyyy"
expr_stmt|;
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|6
condition|)
name|pattern
operator|=
literal|"yyyyMM"
expr_stmt|;
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|8
condition|)
name|pattern
operator|=
literal|"yyyyMMdd"
expr_stmt|;
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|10
condition|)
name|pattern
operator|=
literal|"yyyyMMddHH"
expr_stmt|;
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|12
condition|)
name|pattern
operator|=
literal|"yyyyMMddHHmm"
expr_stmt|;
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|14
condition|)
name|pattern
operator|=
literal|"yyyyMMddHHmmss"
expr_stmt|;
elseif|else
if|if
condition|(
name|dateString
operator|.
name|length
argument_list|()
operator|==
literal|17
condition|)
name|pattern
operator|=
literal|"yyyyMMddHHmmssSSS"
expr_stmt|;
else|else
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Input is not valid date string: "
operator|+
name|dateString
argument_list|,
literal|0
argument_list|)
throw|;
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
name|sdf
operator|.
name|parse
argument_list|(
name|dateString
argument_list|)
decl_stmt|;
return|return
name|date
return|;
block|}
comment|/**    * Limit a date's resolution. For example, the date<code>2004-09-21 13:50:11</code>    * will be changed to<code>2004-09-01 00:00:00</code> when using    *<code>Resolution.MONTH</code>.     *     * @param resolution The desired resolution of the date to be returned    * @return the date with all values more precise than<code>resolution</code>    *  set to 0 or 1    */
DECL|method|round
specifier|public
specifier|static
name|Date
name|round
parameter_list|(
name|Date
name|date
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|round
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|,
name|resolution
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Limit a date's resolution. For example, the date<code>1095767411000</code>    * (which represents 2004-09-21 13:50:11) will be changed to     *<code>1093989600000</code> (2004-09-01 00:00:00) when using    *<code>Resolution.MONTH</code>.    *     * @param resolution The desired resolution of the date to be returned    * @return the date with all values more precise than<code>resolution</code>    *  set to 0 or 1, expressed as milliseconds since January 1, 1970, 00:00:00 GMT    */
DECL|method|round
specifier|public
specifier|static
name|long
name|round
parameter_list|(
name|long
name|time
parameter_list|,
name|Resolution
name|resolution
parameter_list|)
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|time
argument_list|)
expr_stmt|;
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|YEAR
condition|)
block|{
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|MONTH
condition|)
block|{
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_MONTH
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|DAY
condition|)
block|{
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|HOUR
condition|)
block|{
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|MINUTE
condition|)
block|{
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|SECOND
condition|)
block|{
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resolution
operator|==
name|Resolution
operator|.
name|MILLISECOND
condition|)
block|{
comment|// don't cut off anything
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown resolution "
operator|+
name|resolution
argument_list|)
throw|;
block|}
return|return
name|cal
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
DECL|class|Resolution
specifier|public
specifier|static
class|class
name|Resolution
block|{
DECL|field|YEAR
specifier|public
specifier|static
specifier|final
name|Resolution
name|YEAR
init|=
operator|new
name|Resolution
argument_list|(
literal|"year"
argument_list|)
decl_stmt|;
DECL|field|MONTH
specifier|public
specifier|static
specifier|final
name|Resolution
name|MONTH
init|=
operator|new
name|Resolution
argument_list|(
literal|"month"
argument_list|)
decl_stmt|;
DECL|field|DAY
specifier|public
specifier|static
specifier|final
name|Resolution
name|DAY
init|=
operator|new
name|Resolution
argument_list|(
literal|"day"
argument_list|)
decl_stmt|;
DECL|field|HOUR
specifier|public
specifier|static
specifier|final
name|Resolution
name|HOUR
init|=
operator|new
name|Resolution
argument_list|(
literal|"hour"
argument_list|)
decl_stmt|;
DECL|field|MINUTE
specifier|public
specifier|static
specifier|final
name|Resolution
name|MINUTE
init|=
operator|new
name|Resolution
argument_list|(
literal|"minute"
argument_list|)
decl_stmt|;
DECL|field|SECOND
specifier|public
specifier|static
specifier|final
name|Resolution
name|SECOND
init|=
operator|new
name|Resolution
argument_list|(
literal|"second"
argument_list|)
decl_stmt|;
DECL|field|MILLISECOND
specifier|public
specifier|static
specifier|final
name|Resolution
name|MILLISECOND
init|=
operator|new
name|Resolution
argument_list|(
literal|"millisecond"
argument_list|)
decl_stmt|;
DECL|field|resolution
specifier|private
name|String
name|resolution
decl_stmt|;
DECL|method|Resolution
specifier|private
name|Resolution
parameter_list|()
block|{     }
DECL|method|Resolution
specifier|private
name|Resolution
parameter_list|(
name|String
name|resolution
parameter_list|)
block|{
name|this
operator|.
name|resolution
operator|=
name|resolution
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|resolution
return|;
block|}
block|}
block|}
end_class
end_unit
