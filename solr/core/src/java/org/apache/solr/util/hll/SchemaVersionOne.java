begin_unit
begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A concrete {@link ISchemaVersion} representing schema version one.  */
end_comment
begin_class
DECL|class|SchemaVersionOne
class|class
name|SchemaVersionOne
implements|implements
name|ISchemaVersion
block|{
comment|/**      * The schema version number for this instance.      */
DECL|field|SCHEMA_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|SCHEMA_VERSION
init|=
literal|1
decl_stmt|;
comment|// ------------------------------------------------------------------------
comment|// Version-specific ordinals (array position) for each of the HLL types
DECL|field|TYPE_ORDINALS
specifier|private
specifier|static
specifier|final
name|HLLType
index|[]
name|TYPE_ORDINALS
init|=
operator|new
name|HLLType
index|[]
block|{
name|HLLType
operator|.
name|EMPTY
block|,
name|HLLType
operator|.
name|EXPLICIT
block|,
name|HLLType
operator|.
name|SPARSE
block|,
name|HLLType
operator|.
name|FULL
block|}
decl_stmt|;
comment|// ------------------------------------------------------------------------
comment|// number of header bytes for all HLL types
DECL|field|HEADER_BYTE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|HEADER_BYTE_COUNT
init|=
literal|3
decl_stmt|;
comment|// sentinel values from the spec for explicit off and auto
DECL|field|EXPLICIT_OFF
specifier|private
specifier|static
specifier|final
name|int
name|EXPLICIT_OFF
init|=
literal|0
decl_stmt|;
DECL|field|EXPLICIT_AUTO
specifier|private
specifier|static
specifier|final
name|int
name|EXPLICIT_AUTO
init|=
literal|63
decl_stmt|;
comment|// ************************************************************************
comment|/* (non-Javadoc)      * @see net.agkn.hll.serialization.ISchemaVersion#paddingBytes(HLLType)      */
annotation|@
name|Override
DECL|method|paddingBytes
specifier|public
name|int
name|paddingBytes
parameter_list|(
specifier|final
name|HLLType
name|type
parameter_list|)
block|{
return|return
name|HEADER_BYTE_COUNT
return|;
block|}
comment|/* (non-Javadoc)      * @see net.agkn.hll.serialization.ISchemaVersion#writeMetadata(byte[], IHLLMetadata)      */
annotation|@
name|Override
DECL|method|writeMetadata
specifier|public
name|void
name|writeMetadata
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|,
specifier|final
name|IHLLMetadata
name|metadata
parameter_list|)
block|{
specifier|final
name|HLLType
name|type
init|=
name|metadata
operator|.
name|HLLType
argument_list|()
decl_stmt|;
specifier|final
name|int
name|typeOrdinal
init|=
name|getOrdinal
argument_list|(
name|type
argument_list|)
decl_stmt|;
specifier|final
name|int
name|explicitCutoffValue
decl_stmt|;
if|if
condition|(
name|metadata
operator|.
name|explicitOff
argument_list|()
condition|)
block|{
name|explicitCutoffValue
operator|=
name|EXPLICIT_OFF
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|metadata
operator|.
name|explicitAuto
argument_list|()
condition|)
block|{
name|explicitCutoffValue
operator|=
name|EXPLICIT_AUTO
expr_stmt|;
block|}
else|else
block|{
name|explicitCutoffValue
operator|=
name|metadata
operator|.
name|log2ExplicitCutoff
argument_list|()
operator|+
literal|1
comment|/*per spec*/
expr_stmt|;
block|}
name|bytes
index|[
literal|0
index|]
operator|=
name|SerializationUtil
operator|.
name|packVersionByte
argument_list|(
name|SCHEMA_VERSION
argument_list|,
name|typeOrdinal
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
name|SerializationUtil
operator|.
name|packParametersByte
argument_list|(
name|metadata
operator|.
name|registerWidth
argument_list|()
argument_list|,
name|metadata
operator|.
name|registerCountLog2
argument_list|()
argument_list|)
expr_stmt|;
name|bytes
index|[
literal|2
index|]
operator|=
name|SerializationUtil
operator|.
name|packCutoffByte
argument_list|(
name|explicitCutoffValue
argument_list|,
name|metadata
operator|.
name|sparseEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see net.agkn.hll.serialization.ISchemaVersion#readMetadata(byte[])      */
annotation|@
name|Override
DECL|method|readMetadata
specifier|public
name|IHLLMetadata
name|readMetadata
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|)
block|{
specifier|final
name|byte
name|versionByte
init|=
name|bytes
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|byte
name|parametersByte
init|=
name|bytes
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|byte
name|cutoffByte
init|=
name|bytes
index|[
literal|2
index|]
decl_stmt|;
specifier|final
name|int
name|typeOrdinal
init|=
name|SerializationUtil
operator|.
name|typeOrdinal
argument_list|(
name|versionByte
argument_list|)
decl_stmt|;
specifier|final
name|int
name|explicitCutoffValue
init|=
name|SerializationUtil
operator|.
name|explicitCutoff
argument_list|(
name|cutoffByte
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|explicitOff
init|=
operator|(
name|explicitCutoffValue
operator|==
name|EXPLICIT_OFF
operator|)
decl_stmt|;
specifier|final
name|boolean
name|explicitAuto
init|=
operator|(
name|explicitCutoffValue
operator|==
name|EXPLICIT_AUTO
operator|)
decl_stmt|;
specifier|final
name|int
name|log2ExplicitCutoff
init|=
operator|(
name|explicitOff
operator|||
name|explicitAuto
operator|)
condition|?
operator|-
literal|1
comment|/*sentinel*/
else|:
operator|(
name|explicitCutoffValue
operator|-
literal|1
comment|/*per spec*/
operator|)
decl_stmt|;
return|return
operator|new
name|HLLMetadata
argument_list|(
name|SCHEMA_VERSION
argument_list|,
name|getType
argument_list|(
name|typeOrdinal
argument_list|)
argument_list|,
name|SerializationUtil
operator|.
name|registerCountLog2
argument_list|(
name|parametersByte
argument_list|)
argument_list|,
name|SerializationUtil
operator|.
name|registerWidth
argument_list|(
name|parametersByte
argument_list|)
argument_list|,
name|log2ExplicitCutoff
argument_list|,
name|explicitOff
argument_list|,
name|explicitAuto
argument_list|,
name|SerializationUtil
operator|.
name|sparseEnabled
argument_list|(
name|cutoffByte
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see net.agkn.hll.serialization.ISchemaVersion#getSerializer(HLLType, int, int)      */
annotation|@
name|Override
DECL|method|getSerializer
specifier|public
name|IWordSerializer
name|getSerializer
parameter_list|(
name|HLLType
name|type
parameter_list|,
name|int
name|wordLength
parameter_list|,
name|int
name|wordCount
parameter_list|)
block|{
return|return
operator|new
name|BigEndianAscendingWordSerializer
argument_list|(
name|wordLength
argument_list|,
name|wordCount
argument_list|,
name|paddingBytes
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see net.agkn.hll.serialization.ISchemaVersion#getDeserializer(HLLType, int, byte[])      */
annotation|@
name|Override
DECL|method|getDeserializer
specifier|public
name|IWordDeserializer
name|getDeserializer
parameter_list|(
name|HLLType
name|type
parameter_list|,
name|int
name|wordLength
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
operator|new
name|BigEndianAscendingWordDeserializer
argument_list|(
name|wordLength
argument_list|,
name|paddingBytes
argument_list|(
name|type
argument_list|)
argument_list|,
name|bytes
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see net.agkn.hll.serialization.ISchemaVersion#schemaVersionNumber()      */
annotation|@
name|Override
DECL|method|schemaVersionNumber
specifier|public
name|int
name|schemaVersionNumber
parameter_list|()
block|{
return|return
name|SCHEMA_VERSION
return|;
block|}
comment|// ========================================================================
comment|// Type/Ordinal lookups
comment|/**      * Gets the ordinal for the specified {@link HLLType}.      *      * @param  type the type whose ordinal is desired      * @return the ordinal for the specified type, to be used in the version byte.      *         This will always be non-negative.      */
DECL|method|getOrdinal
specifier|private
specifier|static
name|int
name|getOrdinal
parameter_list|(
specifier|final
name|HLLType
name|type
parameter_list|)
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
name|TYPE_ORDINALS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|TYPE_ORDINALS
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
return|return
name|i
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown HLL type "
operator|+
name|type
argument_list|)
throw|;
block|}
comment|/**      * Gets the {@link HLLType} for the specified ordinal.      *      * @param  ordinal the ordinal whose type is desired      * @return the type for the specified ordinal. This will never be<code>null</code>.      */
DECL|method|getType
specifier|private
specifier|static
name|HLLType
name|getType
parameter_list|(
specifier|final
name|int
name|ordinal
parameter_list|)
block|{
if|if
condition|(
operator|(
name|ordinal
operator|<
literal|0
operator|)
operator|||
operator|(
name|ordinal
operator|>=
name|TYPE_ORDINALS
operator|.
name|length
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type ordinal '"
operator|+
name|ordinal
operator|+
literal|"'. Only 0-"
operator|+
operator|(
name|TYPE_ORDINALS
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|" inclusive allowed."
argument_list|)
throw|;
block|}
return|return
name|TYPE_ORDINALS
index|[
name|ordinal
index|]
return|;
block|}
block|}
end_class
end_unit
