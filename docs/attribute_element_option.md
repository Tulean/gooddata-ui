---
title: AttributeElements Options
sidebar_label: AttributeElements Component Options
copyright: (C) 2020 GoodData Corporation
id: attribute_element_option
---

This article describes the options for configuring the [AttributeElements component](create_custom_attribute_filter.md#example).

The AttributeElements options define the behavior of how its values are queried.

## Types of AttributeElements options

All top-level options are optional. You can use only those that are relevant in your project.

| Name | Required? | Type | Description |
| :--- | :--- | :--- | :--- |
| limit | false | number | Limit amount of elements |
| offset | false | number | Offsets starting point of the element list |
| order | false | [SortDirection](result_specification.md#sorting) | Ordering of the elements |
| filter | false | string | Filter elements by text value |
| uris | false | string[] | With this option you can specify concrete attribute elements to load. This is commonly used to preloaded selected elements in the attribute filter. |
| includeTotalCountWithoutFilters | false | boolean | Include the total count of all elements in the response (without filters applied) |
| afm | false | [AFM](afm.md) | Filters valid elements |
