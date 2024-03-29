begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
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
name|index
operator|.
name|IndexableField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|TimestampUpdateProcessorFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DateMathParser
import|;
end_import
begin_comment
comment|/**  * FieldType that can represent any Date/Time with millisecond precision.  *<p>  * Date Format for the XML, incoming and outgoing:  *</p>  *<blockquote>  * A date field shall be of the form 1995-12-31T23:59:59Z  * The trailing "Z" designates UTC time and is mandatory  * (See below for an explanation of UTC).  * Optional fractional seconds are allowed, as long as they do not end  * in a trailing 0 (but any precision beyond milliseconds will be ignored).  * All other parts are mandatory.  *</blockquote>  *<p>  * This format was derived to be standards compliant (ISO 8601) and is a more  * restricted form of the  *<a href="http://www.w3.org/TR/xmlschema-2/#dateTime-canonical-representation">canonical  * representation of dateTime</a> from XML schema part 2.  Examples...  *</p>  *<ul>  *<li>1995-12-31T23:59:59Z</li>  *<li>1995-12-31T23:59:59.9Z</li>  *<li>1995-12-31T23:59:59.99Z</li>  *<li>1995-12-31T23:59:59.999Z</li>  *</ul>  *<p>  * Note that TrieDateField is lenient with regards to parsing fractional  * seconds that end in trailing zeros and will ensure that those values  * are indexed in the correct canonical format.  *</p>  *<p>  * This FieldType also supports incoming "Date Math" strings for computing  * values by adding/rounding internals of time relative either an explicit  * datetime (in the format specified above) or the literal string "NOW",  * ie: "NOW+1YEAR", "NOW/DAY", "1995-12-31T23:59:59.999Z+5MINUTES", etc...  * -- see {@link DateMathParser} for more examples.  *</p>  *<p>  *<b>NOTE:</b> Although it is possible to configure a<code>TrieDateField</code>  * instance with a default value of "<code>NOW</code>" to compute a timestamp  * of when the document was indexed, this is not advisable when using SolrCloud  * since each replica of the document may compute a slightly different value.  * {@link TimestampUpdateProcessorFactory} is recommended instead.  *</p>  *  *<p>  * Explanation of "UTC"...  *</p>  *<blockquote>  * "In 1970 the Coordinated Universal Time system was devised by an  * international advisory group of technical experts within the International  * Telecommunication Union (ITU).  The ITU felt it was best to designate a  * single abbreviation for use in all languages in order to minimize  * confusion.  Since unanimous agreement could not be achieved on using  * either the English word order, CUT, or the French word order, TUC, the  * acronym UTC was chosen as a compromise."  *</blockquote>  *  * @see TrieField  */
end_comment
begin_class
DECL|class|TrieDateField
specifier|public
class|class
name|TrieDateField
extends|extends
name|TrieField
implements|implements
name|DateValueFieldType
block|{
block|{
name|this
operator|.
name|type
operator|=
name|TrieTypes
operator|.
name|DATE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Date
name|toObject
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
return|return
operator|(
name|Date
operator|)
name|super
operator|.
name|toObject
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toNativeType
specifier|public
name|Object
name|toNativeType
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
block|{
return|return
name|DateMathParser
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
operator|(
name|String
operator|)
name|val
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|toNativeType
argument_list|(
name|val
argument_list|)
return|;
block|}
block|}
end_class
end_unit
